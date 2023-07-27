package com.example.homework.service

import com.example.homework.entity.*
import com.example.homework.repository.CarRepository
import com.example.homework.repository.ManufacturerModelRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class CarService(
    private val carRepository: CarRepository,
    private val manufacturerModelRepository: ManufacturerModelRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun addCar(manufacturer: String, model: String, productionYear: Int, vin: String): UUID {
        val isVerified = verifyManufacturerModel(manufacturer, model)

        if(!isVerified)
            throw InvalidManufacturerOrModelException(manufacturer, model)

        val car = Car(
            date = LocalDate.now(),
            carDetails = manufacturerModelRepository.findManufacturerModelByManufacturerAndModel(manufacturer, model),
            productionYear = productionYear,
            vin = vin,
            checkUps = mutableListOf<CarCheckUp>()
        )

        return carRepository.save(car).id
    }

    fun fetchManufacturerAnalytics(): MutableMap<String, Int> {
        var map: MutableMap<String, Int> = mutableMapOf<String, Int>()
        var cars = carRepository.findAll()

        cars.forEach { car ->
            val count = car.checkUps.count()
            if (map.containsKey(car.carDetails.manufacturer)) {
                val newValue = map[car.carDetails.manufacturer]?.plus(count) ?: 0
                map[car.carDetails.manufacturer] = newValue
            } else {
                map[car.carDetails.manufacturer] = count
            }
        }

        return map
    }

    fun fetchDetailsByCarId(carId: UUID): Car? {
        if(!carRepository.existsCarById(carId)) throw CarIdException(carId)

        return carRepository.findCarById(carId)
    }

    fun getAllCarsPaged(pageable: Pageable): Page<Car> {
        return carRepository.findAll(pageable)
    }

    fun isCheckUpNecessary(carId: UUID): Boolean {
        val currentDate = LocalDateTime.now()

        val cars = carRepository.findAll()

        return cars.find { car -> car.id == carId }?.checkUps?.none { checkUp ->
            ChronoUnit.YEARS.between(
                checkUp.performedAt,
                currentDate
            ) < 1
        } ?: true
    }

    @Cacheable("Verification")
    fun verifyManufacturerModel(manufacturer: String, model: String): Boolean {
        logger.info("Caching data for $manufacturer $model")
        return manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(manufacturer, model)
    }
}