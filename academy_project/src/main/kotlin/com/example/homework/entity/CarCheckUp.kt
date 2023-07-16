package com.example.homework.entity

import java.time.LocalDateTime

data class CarCheckUp (
    val id: Long,
    val performedAt: LocalDateTime,
    val worker: String,
    val price: Int,
    val carId: Long
)