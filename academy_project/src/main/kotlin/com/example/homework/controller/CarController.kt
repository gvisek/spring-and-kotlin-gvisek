package com.example.homework.controller

import com.example.homework.controller.dto.CarResourceAssembler
import com.example.homework.entity.CarDetailsResponse
import com.example.homework.entity.CarIdException
import com.example.homework.entity.CarRequest
import com.example.homework.entity.InvalidManufacturerOrModelException
import com.example.homework.service.CarService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Controller
@RequestMapping("/cars")
class CarController(
    private val carService: CarService,
    private val resourceAssembler: CarResourceAssembler
) {
    @PostMapping
    @ResponseBody
    fun addCar(@RequestBody car: CarRequest): ResponseEntity<Any> {
        val carId = carService.addCar(car.manufacturer, car.model, car.productionYear.value, car.vin)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(mapOf("id" to carId))
            .toUri()
        return ResponseEntity.created(location).body("Car with id: $carId created.")
    }

    @GetMapping("/{carId}")
    @ResponseBody
    fun getCarDetails(@PathVariable carId: UUID): ResponseEntity<Any> {
        val car = carService.fetchDetailsByCarId(carId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID $carId not found")
        val carDetailsResponse = CarDetailsResponse(car, carService.isCheckUpNecessary(carId))

        return ResponseEntity.ok(resourceAssembler.toModel(carDetailsResponse))
    }

    @GetMapping("/analytics")
    @ResponseBody
    fun getAnalytics() = carService.fetchManufacturerAnalytics()

    @ExceptionHandler(value = [(CarIdException::class)])
    fun handleCarIdException(ex: CarIdException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [(InvalidManufacturerOrModelException::class)])
    fun handleInvalidManufacturerOrModelException(ex: InvalidManufacturerOrModelException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/page")
    @ResponseBody
    fun getAllCarsPaged(
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<CarDetailsResponse>
    ): ResponseEntity<Any>{
        return ResponseEntity.ok(
            pagedResourcesAssembler.toModel(
                carService.getAllCarsPaged(pageable).map { car -> CarDetailsResponse(car, carService.isCheckUpNecessary(car.id)) },
                resourceAssembler
            )
        )
    }
}