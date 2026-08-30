package com.touf.letsgo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.touf.letsgo.data.local.dao.*
import com.touf.letsgo.data.local.entity.*

@Database(
    entities = [
        PersonEntity::class,
        PhoneNumberEntity::class,
        AddressEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false // met true si tu veux exporter le schéma pour les migrations
)
abstract class LetsGoDatabase : RoomDatabase() {
    // Chaque DAO est accessible via une propriété abstraite
    abstract fun personDao(): PersonDao
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun addressDao(): AddressDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: LetsGoDatabase? = null

        fun getInstance(context: Context): LetsGoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LetsGoDatabase::class.java,
                    "letsgo.db" // nom du fichier de la base de données
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}