package com.example.homework.entity

import java.time.LocalDateTime

data class CarCheckUpRequest(
    val performedAt: LocalDateTime,
    val worker: String,
    val price: Int,
    val carId: Long
)
