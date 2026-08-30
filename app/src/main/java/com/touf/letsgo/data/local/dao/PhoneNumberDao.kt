package com.touf.letsgo.data.local.dao

import androidx.room.*
import com.touf.letsgo.data.local.entity.PhoneNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneNumberDao {
    // Récupère tous les numéros d'une personne
    @Query("SELECT * FROM phone_number WHERE personId = :personId")
    fun getByPersonId(personId: Long): Flow<List<PhoneNumberEntity>>

    // Récupère le numéro principal d'une personne (un seul, car isPrimary = 1)
    @Query("SELECT * FROM phone_number WHERE personId = :personId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryByPersonId(personId: Long): PhoneNumberEntity?

    // Récupère un numéro par son ID
    @Query("SELECT * FROM phone_number WHERE id = :id")
    suspend fun getById(id: Long): PhoneNumberEntity?

    // Insère un numéro
    @Insert
    suspend fun insert(phoneNumber: PhoneNumberEntity): Long

    // Met à jour un numéro
    @Update
    suspend fun update(phoneNumber: PhoneNumberEntity)

    // Supprime un numéro
    @Delete
    suspend fun delete(phoneNumber: PhoneNumberEntity)

    // Désactive le principal pour une personne (passe tous les isPrimary à 0)
    @Query("UPDATE phone_number SET isPrimary = 0 WHERE personId = :personId")
    suspend fun clearPrimaryForPerson(personId: Long)

    // Supprime tous les numéros d'une personne (utilisé lors de la suppression de la personne)
    @Query("DELETE FROM phone_number WHERE personId = :personId")
    suspend fun deleteAllForPerson(personId: Long)
}