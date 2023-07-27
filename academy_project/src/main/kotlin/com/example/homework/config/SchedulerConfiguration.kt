package com.example.homework.config

import com.example.homework.service.ManufacturerModelService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
@Profile("!test")
class SchedulerConfiguration(
    private val manufacturerModelService: ManufacturerModelService
) {

    @Scheduled(fixedDelay = 10000)
    @CacheEvict(allEntries = true, value = ["Verification"])
    fun fetchData(){
        manufacturerModelService.getAllManufacturersAndModels()
    }

}