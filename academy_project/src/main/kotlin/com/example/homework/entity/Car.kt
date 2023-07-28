package com.example.homework.entity

import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name= "cars")
data class Car(
    @Id
    val id: UUID = UUID.randomUUID(),

    val date: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_details")
    val carDetails: ManufacturerModel,

    val productionYear: Int,

    val vin: String,

    @OneToMany(mappedBy = "car", cascade = [CascadeType.ALL], orphanRemoval = true)
    var checkUps: MutableList<CarCheckUp> = mutableListOf()
)
