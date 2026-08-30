package com.touf.letsgo.data.local.dao

import androidx.room.*
import com.touf.letsgo.data.local.entity.PersonEntity
import com.touf.letsgo.data.local.entity.PersonWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM person ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM person WHERE id = :id")
    suspend fun getById(id: Long): PersonEntity?

    @Query("SELECT * FROM person WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<PersonEntity?>

    @Query("SELECT * FROM person WHERE isQuickAccess = 1 ORDER BY quickAccessPosition ASC")
    fun getQuickAccess(): Flow<List<PersonEntity>>

    @Insert
    suspend fun insert(person: PersonEntity): Long

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM person WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM person WHERE searchableName LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PersonEntity>>

    @Transaction
    @Query("SELECT * FROM person ORDER BY name COLLATE NOCASE")
    fun getAllWithDetails(): Flow<List<PersonWithDetails>>

    @Transaction
    @Query("SELECT * FROM person WHERE id = :id")
    fun getByIdWithDetailsFlow(id: Long): Flow<PersonWithDetails?>

    @Transaction
    @Query("SELECT * FROM person WHERE isQuickAccess = 1 ORDER BY quickAccessPosition ASC")
    fun getQuickAccessWithDetails(): Flow<List<PersonWithDetails>>
}