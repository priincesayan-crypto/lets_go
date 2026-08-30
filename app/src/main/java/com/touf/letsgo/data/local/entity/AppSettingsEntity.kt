package com.touf.letsgo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 0, // une seule ligne, toujours id=0
    val homeAddress: String? = null,
    val workAddress: String? = null,
    val preferredNavApp: String? = null,
    val gridSize: Int = 6,
    val quickAccessLimit: Int = 4
)