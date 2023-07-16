package com.example.homework.entity

import java.time.LocalDate
import java.time.Year

data class CarRequest (
    val manufacturer: String,
    val model: String,
    val productionYear: Year,
    val vin: String
    )