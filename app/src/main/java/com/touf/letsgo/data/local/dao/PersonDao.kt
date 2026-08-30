package com.touf.letsgo.data.local.dao

import androidx.room.*
import com.touf.letsgo.data.local.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    // Récupère toutes les personnes triées par nom (sans tenir compte de la casse)
    @Query("SELECT * FROM person ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<PersonEntity>>

    // Récupère une personne par son ID (ponctuel, hors Flow)
    @Query("SELECT * FROM person WHERE id = :id")
    suspend fun getById(id: Long): PersonEntity?

    // NOUVEAU : récupère une personne par son ID sous forme de Flow réactif,
    // SANS charger tout le carnet — indispensable pour les gros carnets (1000+ contacts)
    @Query("SELECT * FROM person WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<PersonEntity?>

    // Récupère les personnes marquées "quick access" (les 6 raccourcis)
    @Query("SELECT * FROM person WHERE isQuickAccess = 1 ORDER BY quickAccessPosition ASC")
    fun getQuickAccess(): Flow<List<PersonEntity>>

    // Insère une personne et retourne son ID généré
    @Insert
    suspend fun insert(person: PersonEntity): Long

    // Met à jour une personne
    @Update
    suspend fun update(person: PersonEntity)

    // Supprime une personne
    @Delete
    suspend fun delete(person: PersonEntity)

    // Supprime une personne par son ID
    @Query("DELETE FROM person WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Recherche les personnes dont le nom contient la chaîne (insensible à la casse et aux accents)
    @Query("SELECT * FROM person WHERE searchableName LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PersonEntity>>
}