package com.example.homework.controller

import com.example.homework.entity.CarCheckUpRequest
import com.example.homework.entity.CarDetailsResponse
import com.example.homework.entity.CarIdException
import com.example.homework.entity.CheckUpException
import com.example.homework.service.CheckUpService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Controller
@RequestMapping("/cars/checkup")
class CheckUpController(
    private val checkUpService: CheckUpService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<CarDetailsResponse>
) {
    @PostMapping
    @ResponseBody
    fun addCarCheckUp(@RequestBody carCheckUp: CarCheckUpRequest): ResponseEntity<Any> {
        val checkUpId = checkUpService.addCarCheckUp(carCheckUp.performedAt, carCheckUp.worker, carCheckUp.price, carCheckUp.carId)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}/page")
            .buildAndExpand(mapOf("id" to carCheckUp.carId))
            .toUri()
        return ResponseEntity.created(location).body("Car Check Up with id: $checkUpId added")
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteCheckUp(@PathVariable id: UUID): ResponseEntity<Any>{
        val checkUp = checkUpService.deleteCarCheckUp(id)
        return ResponseEntity.ok("Deleted checkUp with id: ${checkUp?.id}")
    }

    @GetMapping("/{carId}/page")
    @ResponseBody
    fun getAllCheckUpsForCarPaged(@PathVariable carId: UUID,@RequestParam sortOrder: String?, pageable: Pageable): ResponseEntity<Any>{
        val checkUps = when (sortOrder) {
            "asc" -> checkUpService.getAllCheckUpsForCarPaged(carId, pageable).sortedBy { it.performedAt }
            "desc" -> checkUpService.getAllCheckUpsForCarPaged(carId, pageable).sortedByDescending { it.performedAt }
            else -> checkUpService.getAllCheckUpsForCarPaged(carId, pageable)
        }


        val carLink = Link.of(linkTo<CarController> { getCarDetails(carId) }.toString(), "carDetails")

        val allCarsLink = Link.of(linkTo<CarController> { getAllCarsPaged(pageable = pageable, pagedResourcesAssembler = pagedResourcesAssembler) }.toString(), "allCars")

        val collectionModel = CollectionModel.of(checkUps, carLink, allCarsLink)


        return ResponseEntity.ok(collectionModel)
    }

    @ExceptionHandler(value = [(CarIdException::class)])
    fun handleException(ex: CarIdException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [(CheckUpException::class)])
    fun handleCheckUpIdException(ex: CheckUpException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }
}