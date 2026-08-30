package com.touf.letsgo.data.local.dao

import androidx.room.*
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    // Récupère les paramètres (une seule ligne)
    @Query("SELECT * FROM app_settings LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    // Insère ou remplace les paramètres (en cas de conflit)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: AppSettingsEntity)

    // Met à jour les paramètres
    @Update
    suspend fun update(settings: AppSettingsEntity)
}