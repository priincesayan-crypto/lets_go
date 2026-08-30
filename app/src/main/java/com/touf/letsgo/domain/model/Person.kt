package com.touf.letsgo.domain.model

data class Person(
    val id: Long,
    val name: String,
    val photoUri: String?,
    val notes: String?,
    val isQuickAccess: Boolean,
    val quickAccessPosition: Int?,
    val searchableName: String,
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
) {
    // Propriétés calculées pour accéder facilement au principal
    val primaryPhoneNumber: PhoneNumber?
        get() = phoneNumbers.firstOrNull { it.isPrimary }

    val primaryAddress: Address?
        get() = addresses.firstOrNull { it.isPrimary }
}