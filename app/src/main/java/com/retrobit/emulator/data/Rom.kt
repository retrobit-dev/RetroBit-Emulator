package com.retrobit.emulator.data

data class Rom(
    val id: String,
    val name: String,
    val uri: String,
    val system: String,
    val sizeBytes: Long,
    val addedAt: Long = System.currentTimeMillis(),
)
