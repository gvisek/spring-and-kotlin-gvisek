package com.example.homework.repository

import com.example.homework.entity.ManufacturerModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ManufacturerModelRepository: JpaRepository<ManufacturerModel, UUID> {

    fun save(manufacturerModel: ManufacturerModel): ManufacturerModel

    fun existsManufacturerModelByManufacturerAndModel(manufacturer: String, model: String): Boolean

    fun findManufacturerModelByManufacturerAndModel(manufacturer: String, model: String): ManufacturerModel

    override fun findAll(): List<ManufacturerModel>

}