package com.example.homework.service

import com.example.homework.entity.ManufacturerModel
import com.example.homework.entity.ManufacturerModelResponse
import com.example.homework.repository.ManufacturerModelRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class ManufacturerModelService(
    private val manufacturerModelRepository: ManufacturerModelRepository,
    private val webClient: WebClient,
    @Value("\${car-service.base-url}") private val baseUrl: String
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllManufacturersAndModels(){
        logger.info("Fetching data for car Manufactuers and Models")
        webClient.get().uri("$baseUrl/api/v1/cars/1").retrieve()
            .bodyToMono<ManufacturerModelResponse>()
            .map { manufacturerModelResponse -> manufacturerModelResponse.cars}
            .flatMapIterable { cars -> cars }
            .doOnNext{carDetail ->
                carDetail.models.forEach{model ->
                    if(!manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(carDetail.manufactuer, model)){
                        manufacturerModelRepository.save(ManufacturerModel(manufacturer = carDetail.manufactuer, model = model))
                    }
                }
            }
            .blockLast()
    }
}