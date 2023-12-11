package com.example.criminalintentchapter15.repo

import android.content.Context
import androidx.room.Room
import com.example.criminalintentchapter15.Crime
import com.example.criminalintentchapter15.database.CrimeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    private val crimeDatabase = Room
        .databaseBuilder(context, CrimeDatabase::class.java, DATABASE_NAME)
        .build()

    fun getCrimes() = crimeDatabase.crimeDao().getCrimes()
    suspend fun getCrime(id: UUID) = crimeDatabase.crimeDao().getCrime(id)
    fun updateCrime(crime: Crime) {
        coroutineScope.launch {
            crimeDatabase.crimeDao().updateCrime(crime)
        }
    }

    suspend fun addCrime(crime: Crime) {
        crimeDatabase.crimeDao().addCrime(crime)
    }

    companion object {
        private var INSTANCE : CrimeRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository = INSTANCE
            ?: throw IllegalStateException("CrimeRepository must be initialized.")
    }
}