package com.example.homework.service

import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CheckUpService(
    private val carRepository: CarRepository,
    private val checkUpsRepository: CheckUpsRepository
) {

    fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: UUID): UUID {
        doesCarExist(carId)

        val carCheckUp = CarCheckUp(
            performedAt = performedAt,
            worker = worker,
            price = price,
            car = carRepository.findCarById(carId) ?: throw CarIdException(carId)
        )

        return checkUpsRepository.save(carCheckUp).id
    }

    fun getAllCheckUpsForCarPaged(carId: UUID, pageable: Pageable): Page<CarCheckUp> {
        doesCarExist(carId)

        return checkUpsRepository.findAllByCarId(carId, pageable)
    }

    fun doesCarExist(carId: UUID) {
        val cars = carRepository.findAll()

        val car = cars.find { car -> car.id == carId } ?: throw CarIdException(carId)
    }
}