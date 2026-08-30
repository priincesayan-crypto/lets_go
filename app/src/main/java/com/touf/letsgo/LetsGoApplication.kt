package com.touf.letsgo

import android.app.Application
import com.touf.letsgo.data.local.LetsGoDatabase
import com.touf.letsgo.data.repository.PersonRepository

/**
 * Classe Application personnalisée.
 * Elle garantit qu'il n'existe qu'UNE seule instance de la base de données
 * et du repository pendant toute la durée de vie de l'application,
 * partagée par tous les écrans/ViewModels.
 *
 * IMPORTANT : cette classe doit être déclarée dans AndroidManifest.xml
 * avec android:name=".LetsGoApplication" sur la balise <application>,
 * sinon elle n'est jamais utilisée par le système.
 */
class LetsGoApplication : Application() {

    val database: LetsGoDatabase by lazy {
        LetsGoDatabase.getInstance(this)
    }

    val repository: PersonRepository by lazy {
        PersonRepository(database)
    }
}