package com.example.homework.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "checkups")
data class CarCheckUp(
    @Id
    val id: UUID = UUID.randomUUID(),

    val performedAt: LocalDateTime,

    val worker: String,

    val price: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    @JsonIgnore
    val car: Car

)
