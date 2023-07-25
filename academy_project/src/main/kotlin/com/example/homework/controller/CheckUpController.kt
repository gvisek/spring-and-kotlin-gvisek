package com.example.homework.controller

import com.example.homework.entity.CarCheckUpRequest
import com.example.homework.entity.CarIdException
import com.example.homework.service.CheckUpService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class CheckUpController(
    private val checkUpService: CheckUpService
) {
    @PostMapping("/cars/checkup")
    @ResponseBody
    fun addCarCheckUp(@RequestBody carCheckUp: CarCheckUpRequest): ResponseEntity<Any> {
        val checUpId = checkUpService.addCarCheckUp(carCheckUp.performedAt, carCheckUp.worker, carCheckUp.price, carCheckUp.carId)

        return ResponseEntity.status(HttpStatus.OK).body("Car Check Up with id: $checUpId added")
    }

    @GetMapping("/cars/checkup/paged/{carId}")
    @ResponseBody
    fun getAllCheckUpsForCarPaged(@PathVariable carId: UUID, pageable: Pageable): ResponseEntity<Any>{
        return ResponseEntity.ok(checkUpService.getAllCheckUpsForCarPaged(carId, pageable))
    }

    @ExceptionHandler(value = [(CarIdException::class)])
    fun handleException(ex: CarIdException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }
}