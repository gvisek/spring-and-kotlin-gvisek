package com.example.homework.service

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.temporal.ChronoUnit


@Service
class CarCheckUpService() {
    private val cars = mutableMapOf<Long, Car>()
    private val carCheckUps = mutableMapOf<Long, CarCheckUp>()

    fun addCar(manufacturer: String, model: String, productionYear: Year, vin: String): Boolean{
        val id = (cars.keys.maxOrNull() ?: 0) + 1
        cars[id] = Car(id, LocalDate.now(), manufacturer, model, productionYear, vin, mutableListOf<CarCheckUp>())
        return true
    }

    fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: Long): Boolean{
        doesCarExist(carId)

        val id = (carCheckUps.keys.maxOrNull() ?: 0) + 1
        val newCheckUp = CarCheckUp(id, performedAt, worker, price, carId)

        cars[carId]?.checkUps?.add(newCheckUp)
        cars[carId]?.checkUps?.sortByDescending { it.performedAt }
        carCheckUps[id] = newCheckUp

        return true
    }

    fun fetchDetailsByCarId(carId: Long): Car?{
        return cars[carId]
    }

    fun fetchManufacturerAnalytics(): MutableMap<String, Int>{
        var map:MutableMap<String, Int> = mutableMapOf<String, Int>()

        cars.values.forEach{car ->
            val count = carCheckUps.values.count { checkUp -> checkUp.carId == car.id }
            if(map.containsKey(car.manufacturer)){
                map[car.manufacturer]?.plus(count)
            }else{
                map[car.manufacturer] = count
            }
        }

        return map
    }

    fun isCheckUpNecessary(carId: Long): Boolean{
        val currentDate = LocalDateTime.now()

        return cars[carId]?.checkUps?.none { checkUp ->
            ChronoUnit.YEARS.between(
                checkUp.performedAt,
                currentDate
            ) < 1
        } ?: true
    }

    fun doesCarExist(carId: Long){
        if(!cars.containsKey(carId))
            throw CarIdException(carId)
    }
}
