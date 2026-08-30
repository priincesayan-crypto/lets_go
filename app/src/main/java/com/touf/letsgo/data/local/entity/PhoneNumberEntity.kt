package com.touf.letsgo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.touf.letsgo.domain.model.PhoneNumber

@Entity(
    tableName = "phone_numbers",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long,
    val rawNumber: String,
    val label: String?,
    val isPrimary: Boolean
) {
    fun toDomain(): PhoneNumber {
        return PhoneNumber(
            id = id,
            rawNumber = rawNumber,
            label = label,
            isPrimary = isPrimary
        )
    }

    companion object {
        fun fromDomain(domain: PhoneNumber, personId: Long): PhoneNumberEntity {
            return PhoneNumberEntity(
                id = domain.id,
                personId = personId,
                rawNumber = domain.rawNumber,
                label = domain.label,
                isPrimary = domain.isPrimary
            )
        }
    }
}