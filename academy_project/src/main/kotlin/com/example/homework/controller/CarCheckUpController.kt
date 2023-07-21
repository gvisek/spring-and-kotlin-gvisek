package com.example.homework.controller

import com.example.homework.entity.CarCheckUpRequest
import com.example.homework.entity.CarDetailsResponse
import com.example.homework.entity.CarIdException
import com.example.homework.entity.CarRequest
import com.example.homework.service.CarCheckUpService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class CarCheckUpController(private val carCheckUpService: CarCheckUpService) {

    @PostMapping("/cars")
    @ResponseBody
    fun addCar(@RequestBody car: CarRequest): ResponseEntity<Any> {
        carCheckUpService.addCar(car.manufacturer, car.model, car.productionYear, car.vin)

        return ResponseEntity.status(HttpStatus.OK).body("Car added")
    }

    @PostMapping("/cars/checkup")
    @ResponseBody
    fun addCarCheckUp(@RequestBody carCheckUp: CarCheckUpRequest): ResponseEntity<Any> {
        carCheckUpService.addCarCheckUp(carCheckUp.performedAt, carCheckUp.worker, carCheckUp.price, carCheckUp.carId)

        return ResponseEntity.status(HttpStatus.OK).body("Car Check Up added")
    }

    @ExceptionHandler(value = [(CarIdException::class)])
    fun handleException(ex: CarIdException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/cars/{carId}")
    @ResponseBody
    fun getCarDetails(@PathVariable carId: Long): ResponseEntity<Any> {
        val car = carCheckUpService.fetchDetailsByCarId(carId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID $carId not found")
        val carDetailsResponse = CarDetailsResponse(car, carCheckUpService.isCheckUpNecessary(carId))

        return ResponseEntity.ok(carDetailsResponse)
    }

    @GetMapping("/analytics")
    @ResponseBody
    fun getAnalytics() = carCheckUpService.fetchManufacturerAnalytics()
}
