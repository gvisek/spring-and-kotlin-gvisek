package com.example.homework.service

import com.example.homework.entity.Car
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarsRepostiory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.Year
import java.time.temporal.ChronoUnit

@Service
class CarCheckUpService(private val carRepository: CarsRepostiory) {

    fun addCar(manufacturer: String, model: String, productionYear: Year, vin: String): Boolean {
        carRepository.addCar(manufacturer, model, productionYear, vin)
        return true
    }

    fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: Long): Boolean {
        carRepository.addCarCheckUp(performedAt, worker, price, carId)

        return true
    }

    fun fetchDetailsByCarId(carId: Long): Car? {
        return carRepository.getCarById(carId)
    }

    fun fetchManufacturerAnalytics(): MutableMap<String, Int> {
        var map: MutableMap<String, Int> = mutableMapOf<String, Int>()
        var cars = carRepository.getAllCars()

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

    fun isCheckUpNecessary(carId: Long): Boolean {
        val currentDate = LocalDateTime.now()

        val cars = carRepository.getAllCars()

        return cars.find { car -> car.id == carId }?.checkUps?.none { checkUp ->
            ChronoUnit.YEARS.between(
                checkUp.performedAt,
                currentDate
            ) < 1
        } ?: true
    }

    fun doesCarExist(carId: Long) {
        val cars = carRepository.getAllCars()

        val car = cars.find { car -> car.id == carId } ?: throw CarIdException(carId)
    }
}
