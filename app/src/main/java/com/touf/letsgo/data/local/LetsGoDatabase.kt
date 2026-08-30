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
    version = 2,
    exportSchema = false
)
abstract class LetsGoDatabase : RoomDatabase() {
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
                    "letsgo.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}