package com.example.homework.service

import com.example.homework.repository.ManufacturerModelRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ManufacturerVerificationService (
    private val manufacturerModelRepository: ManufacturerModelRepository
    ){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable("Verification")
    fun verifyManufacturerModel(manufacturer: String, model: String): Boolean {
        logger.info("Caching data for $manufacturer $model")
        return manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(manufacturer, model)
    }

}