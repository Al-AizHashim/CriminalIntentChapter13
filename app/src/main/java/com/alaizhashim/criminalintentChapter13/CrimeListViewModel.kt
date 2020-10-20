package com.alaizhashim.criminalintentChapter13

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel(){
    private val crimeRepository =
        CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}