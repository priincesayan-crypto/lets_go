package com.touf.letsgo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val photoUri: String? = null,
    val notes: String? = null,
    val isQuickAccess: Boolean = false,
    val quickAccessPosition: Int? = null, // 0..5, null si pas dans les raccourcis
    val searchableName: String, // version normalisée pour la recherche
    val createdAt: Long,
    val updatedAt: Long
)