package com.touf.letsgo.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PersonWithDetails(
    @Embedded val person: PersonEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val phoneNumbers: List<PhoneNumberEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val addresses: List<AddressEntity>
)