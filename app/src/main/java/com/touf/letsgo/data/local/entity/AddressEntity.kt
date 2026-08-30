package com.touf.letsgo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.touf.letsgo.domain.model.Address

@Entity(
    tableName = "addresses",
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
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long,
    val rawAddress: String,
    val label: String?,
    val isPrimary: Boolean
) {
    fun toDomain(): Address {
        return Address(
            id = id,
            rawAddress = rawAddress,
            label = label,
            isPrimary = isPrimary
        )
    }

    companion object {
        fun fromDomain(domain: Address, personId: Long): AddressEntity {
            return AddressEntity(
                id = domain.id,
                personId = personId,
                rawAddress = domain.rawAddress,
                label = domain.label,
                isPrimary = domain.isPrimary
            )
        }
    }
}