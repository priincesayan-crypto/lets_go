package com.touf.letsgo.data.local.repository

import com.touf.letsgo.data.local.dao.SettingsDao
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDao: SettingsDao) {

    fun getSettings(): Flow<AppSettingsEntity?> {
        return settingsDao.getSettings()
    }

    suspend fun saveSettings(settings: AppSettingsEntity) {
        settingsDao.insert(settings)
    }
}