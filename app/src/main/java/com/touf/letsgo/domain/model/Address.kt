package com.touf.letsgo.domain.model

data class Address(
    val id: Long,
    val rawAddress: String,
    val label: String?,
    val isPrimary: Boolean
)