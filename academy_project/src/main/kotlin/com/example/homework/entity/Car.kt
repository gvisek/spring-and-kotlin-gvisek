package com.example.homework.entity

import java.time.LocalDate
import java.time.Year

data class Car(
    val id: Long,
    val date: LocalDate,
    val manufacturer: String,
    val model: String,
    val productionYear: Year,
    val vin: String,
    var checkUps: MutableList<CarCheckUp>
)
