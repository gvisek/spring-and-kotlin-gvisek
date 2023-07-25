package com.example.homework.controller

import com.example.homework.entity.CarDetailsResponse
import com.example.homework.entity.CarIdException
import com.example.homework.entity.CarRequest
import com.example.homework.service.CarService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class CarController(
    private val carService: CarService,
) {
    @PostMapping("/cars")
    @ResponseBody
    fun addCar(@RequestBody car: CarRequest): ResponseEntity<Any> {
        val carid = carService.addCar(car.manufacturer, car.model, car.productionYear.value, car.vin)

        return ResponseEntity.status(HttpStatus.OK).body("Car with id: $carid added")
    }

    @GetMapping("/cars/{carId}")
    @ResponseBody
    fun getCarDetails(@PathVariable carId: UUID): ResponseEntity<Any> {
        val car = carService.fetchDetailsByCarId(carId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID $carId not found")
        val carDetailsResponse = CarDetailsResponse(car, carService.isCheckUpNecessary(carId))

        return ResponseEntity.ok(carDetailsResponse)
    }

    @GetMapping("/analytics")
    @ResponseBody
    fun getAnalytics() = carService.fetchManufacturerAnalytics()

    @ExceptionHandler(value = [(CarIdException::class)])
    fun handleException(ex: CarIdException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/cars/paged")
    @ResponseBody
    fun getAllCarsPaged(pageable: Pageable): ResponseEntity<Any>{
        return ResponseEntity.ok(carService.getAllCarsPaged(pageable))
    }
}