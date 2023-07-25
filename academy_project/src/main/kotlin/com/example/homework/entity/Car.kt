package com.example.homework.entity

import java.time.LocalDate
import java.time.Year
import java.util.*
import javax.persistence.*

@Entity
@Table(name= "cars")
data class Car(
    @Id
    val id: UUID = UUID.randomUUID(),

    val date: LocalDate,

    val manufacturer: String,

    val model: String,

    val productionYear: Int,

    val vin: String,

    @OneToMany(mappedBy = "car", cascade = [CascadeType.ALL], orphanRemoval = true)
    var checkUps: MutableList<CarCheckUp> = mutableListOf()
)
