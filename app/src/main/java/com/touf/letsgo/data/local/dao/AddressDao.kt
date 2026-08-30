package com.touf.letsgo.data.local.dao

import androidx.room.*
import com.touf.letsgo.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    // Récupère toutes les adresses d'une personne
    @Query("SELECT * FROM address WHERE personId = :personId")
    fun getByPersonId(personId: Long): Flow<List<AddressEntity>>

    // Récupère l'adresse principale d'une personne
    @Query("SELECT * FROM address WHERE personId = :personId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryByPersonId(personId: Long): AddressEntity?

    // Récupère une adresse par son ID
    @Query("SELECT * FROM address WHERE id = :id")
    suspend fun getById(id: Long): AddressEntity?

    // Insère une adresse
    @Insert
    suspend fun insert(address: AddressEntity): Long

    // Met à jour une adresse
    @Update
    suspend fun update(address: AddressEntity)

    // Supprime une adresse
    @Delete
    suspend fun delete(address: AddressEntity)

    // Désactive l'adresse principale pour une personne
    @Query("UPDATE address SET isPrimary = 0 WHERE personId = :personId")
    suspend fun clearPrimaryForPerson(personId: Long)

    // Supprime toutes les adresses d'une personne
    @Query("DELETE FROM address WHERE personId = :personId")
    suspend fun deleteAllForPerson(personId: Long)
}