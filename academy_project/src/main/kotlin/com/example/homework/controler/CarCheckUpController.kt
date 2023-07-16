package com.example.homework.controler

import com.example.homework.entity.*
import com.example.homework.service.CarCheckUpService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class CarCheckUpController(private val carCheckUpService: CarCheckUpService) {

    @PostMapping("/add-car")
    @ResponseBody
    fun addCar(@RequestBody car: CarRequest ): ResponseEntity<Any> {
        carCheckUpService.addCar(car.manufacturer, car.model, car.productionYear, car.vin)
        return ResponseEntity.status(HttpStatus.OK).body("Car added")
    }

    @PostMapping("/add-carCheckUp")
    @ResponseBody
    fun addCarCheckUp(@RequestBody carCheckUp: CarCheckUpRequest): ResponseEntity<Any> {
        if(!carCheckUpService.addCarCheckUp(carCheckUp.performedAt, carCheckUp.worker, carCheckUp.price, carCheckUp.carId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID ${carCheckUp.carId} not found")
        else
            return ResponseEntity.status(HttpStatus.OK).body("Car Check Up added")
    }

    @GetMapping("/getCar/{carId}")
    @ResponseBody
    fun getCarDetails(@PathVariable carId: Long): ResponseEntity<Any>{
        val car = carCheckUpService.fetchDetails(carId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID $carId not found")
        val carDetailsResponse = CarDetailsResponse(car, carCheckUpService.isCheckUpNecessary(carId))
        return ResponseEntity.ok(carDetailsResponse)
    }

    @GetMapping("/analytics")
    @ResponseBody
    fun getAnalytics() = carCheckUpService.fetchManufacturerAnalytics()
}