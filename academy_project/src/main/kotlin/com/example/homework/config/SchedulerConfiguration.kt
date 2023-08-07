package com.example.homework.config

import com.example.homework.service.ManufacturerModelService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["scheduling.enabled"], havingValue = "true")
class SchedulerConfiguration(
    private val manufacturerModelService: ManufacturerModelService
) {

    @Scheduled(fixedDelay = 86400000)
    @CacheEvict(allEntries = true, value = ["Verification"])
    fun fetchData(){
        manufacturerModelService.getAllManufacturersAndModels()
    }

}