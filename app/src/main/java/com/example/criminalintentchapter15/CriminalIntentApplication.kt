package com.example.criminalintentchapter15

import android.app.Application
import com.example.criminalintentchapter15.repo.CrimeRepository

class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this.applicationContext)
    }
}