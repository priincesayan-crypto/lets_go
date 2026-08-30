package com.touf.letsgo.domain.model

data class PhoneNumber(
    val id: Long,
    val rawNumber: String,
    val label: String?,
    val isPrimary: Boolean
)