package com.example.homework.service

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class CarService(
    private val carRepository: CarRepository
) {

    fun addCar(manufacturer: String, model: String, productionYear: Int, vin: String): UUID {
        val car = Car(
            date = LocalDate.now(),
            manufacturer = manufacturer,
            model = model,
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
            if (map.containsKey(car.manufacturer)) {
                val newValue = map[car.manufacturer]?.plus(count) ?: 0
                map[car.manufacturer] = newValue
            } else {
                map[car.manufacturer] = count
            }
        }

        return map
    }

    fun fetchDetailsByCarId(carId: UUID): Car? {
        doesCarExist(carId)

        return carRepository.findCarById(carId) ?: throw CarIdException(carId)
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

    fun doesCarExist(carId: UUID) {
        val cars = carRepository.findAll()

        val car = cars.find { car -> car.id == carId } ?: throw CarIdException(carId)
    }
}