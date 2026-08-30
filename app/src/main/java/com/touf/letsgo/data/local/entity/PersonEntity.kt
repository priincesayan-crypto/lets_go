package com.touf.letsgo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.touf.letsgo.domain.model.Person

@Entity(tableName = "person")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val photoUri: String? = null,
    val notes: String? = null,
    val isQuickAccess: Boolean = false,
    val quickAccessPosition: Int? = null,
    val searchableName: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Person {
        return Person(
            id = id,
            name = name,
            photoUri = photoUri,
            notes = notes,
            isQuickAccess = isQuickAccess,
            quickAccessPosition = quickAccessPosition,
            searchableName = searchableName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            phoneNumbers = emptyList(),
            addresses = emptyList()
        )
    }

    companion object {
        fun fromDomain(domain: Person): PersonEntity {
            return PersonEntity(
                id = domain.id,
                name = domain.name,
                photoUri = domain.photoUri,
                notes = domain.notes,
                isQuickAccess = domain.isQuickAccess,
                quickAccessPosition = domain.quickAccessPosition,
                searchableName = domain.searchableName,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt
            )
        }
    }
}