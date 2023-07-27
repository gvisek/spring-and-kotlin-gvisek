package com.example.homework.entity

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "manufacturer_model")
data class ManufacturerModel (

    @Id
    val id: UUID = UUID.randomUUID(),

    val manufacturer: String,

    val model: String
)