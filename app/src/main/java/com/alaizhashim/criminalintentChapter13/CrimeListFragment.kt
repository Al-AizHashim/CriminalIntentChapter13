package com.alaizhashim.criminalintentChapter13

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alaizhashim.criminalintent.R
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"
class CrimeListFragment : Fragment() {
    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    } private var callbacks: Callbacks? = null
    @SuppressLint("SimpleDateFormat")
    var formatter: SimpleDateFormat=SimpleDateFormat ("EEEE, MMM d, yyyy")
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer {crimes ->
                crimes?.let {
                    //Log.i(TAG, "Got crimes ${crimes.size}")
                     //adapter?.submitList(crimes)
                    updateUI(crimes)

                }
            })
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        var xx: List<Crime>? =null
        crimeRecyclerView.adapter = adapter
        //adapter=CrimeAdapter().submitList((crimes))

        var listAdapter=crimeRecyclerView.adapter as CrimeAdapter

        listAdapter.submitList(crimes)


    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
    ///////////////////////
    abstract inner class MainHolder(view: View) : RecyclerView.ViewHolder(view){
        abstract fun bind(crime: Crime)
    }
    /////////////////////////////////////////////
    private inner class CrimeHolder(view: View) : MainHolder(view), View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        init {
            itemView.setOnClickListener(this)
        }
        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text =formatter.format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
    ////////////////////////////////////////////////////////////////////////
    private inner class SeriousCrimeHolder(view: View) : MainHolder(view), View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        private val contact_police_button: Button = itemView.findViewById(R.id.contact_police_button)
        init {
            itemView.setOnClickListener(this)
        }
        @SuppressLint("SimpleDateFormat")
        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text =formatter.format(this.crime.date)
            contact_police_button.apply {
                text="Contact Police"
            }
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
            contact_police_button.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
    ///////////////////////////////////////////
    private inner class CrimeAdapter(var crimes: List<Crime>): ListAdapter<Crime, MainHolder>(CrimeDiffUtil()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : MainHolder {
            //  var  view:View
            when(viewType) {
                1-> {
                    val view=layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false)
                    return SeriousCrimeHolder(view)
                }
                else -> {
                    val view=layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    return CrimeHolder(view)
                }
            }
        }

        override fun getItemCount() = crimes.size
        override fun onBindViewHolder(holder: MainHolder, position: Int) {
            val crime = getItem(position)

            holder.bind(crime)
        }

        override fun getItemViewType(position: Int): Int {
            val x:Int
           // if (!crimes[position].isSolved)
            if (false)
                x=1
            else
                x=0
            return x
        }
    }
    private inner class CrimeDiffUtil:DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

    }

}