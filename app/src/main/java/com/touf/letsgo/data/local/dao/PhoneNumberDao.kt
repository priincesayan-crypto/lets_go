package com.touf.letsgo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.touf.letsgo.data.local.entity.PhoneNumberEntity

@Dao
interface PhoneNumberDao {

    @Query("SELECT * FROM phone_numbers WHERE personId = :personId")
    suspend fun getPhonesForPerson(personId: Long): List<PhoneNumberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhones(phones: List<PhoneNumberEntity>)

    @Query("UPDATE phone_numbers SET isPrimary = 0 WHERE personId = :personId")
    suspend fun clearPrimaryForPerson(personId: Long)

    @Query("UPDATE phone_numbers SET isPrimary = 1 WHERE id = :phoneId")
    suspend fun setPrimaryPhoneDirect(phoneId: Long)

    @Transaction
    suspend fun setPrimaryPhone(personId: Long, phoneId: Long) {
        clearPrimaryForPerson(personId)
        setPrimaryPhoneDirect(phoneId)
    }
}