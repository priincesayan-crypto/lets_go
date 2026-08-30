package com.touf.letsgo

import android.app.Application
import com.touf.letsgo.data.local.LetsGoDatabase
import com.touf.letsgo.data.local.repository.PersonRepository
import com.touf.letsgo.data.local.repository.SettingsRepository

class LetsGoApplication : Application() {

    val database: LetsGoDatabase by lazy {
        LetsGoDatabase.getInstance(this)
    }

    val repository: PersonRepository by lazy {
        PersonRepository(
            personDao = database.personDao(),
            phoneNumberDao = database.phoneNumberDao(),
            addressDao = database.addressDao()
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(database.settingsDao())
    }
}