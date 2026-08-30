package com.touf.letsgo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.touf.letsgo.data.local.entity.AddressEntity

@Dao
interface AddressDao {

    @Query("SELECT * FROM addresses WHERE personId = :personId")
    suspend fun getAddressesForPerson(personId: Long): List<AddressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Query("UPDATE addresses SET isPrimary = 0 WHERE personId = :personId")
    suspend fun clearPrimaryForPerson(personId: Long)

    @Query("UPDATE addresses SET isPrimary = 1 WHERE id = :addressId")
    suspend fun setPrimaryAddressDirect(addressId: Long)

    @Transaction
    suspend fun setPrimaryAddress(personId: Long, addressId: Long) {
        clearPrimaryForPerson(personId)
        setPrimaryAddressDirect(addressId)
    }
}