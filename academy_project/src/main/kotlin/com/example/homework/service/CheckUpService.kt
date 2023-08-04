package com.example.homework.service

import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.entity.CheckUpException
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class CheckUpService(
    private val carRepository: CarRepository,
    private val checkUpsRepository: CheckUpsRepository
) {

    fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: UUID): UUID {
        if(!carRepository.existsCarById(carId)) throw CarIdException(carId)

        val carCheckUp = CarCheckUp(
            performedAt = performedAt,
            worker = worker,
            price = price,
            car = carRepository.findCarById(carId) ?: throw CarIdException(carId)
        )

        return checkUpsRepository.save(carCheckUp).id
    }

    @Transactional
    fun deleteCarCheckUp(id: UUID): CarCheckUp{
        if(!checkUpsRepository.existsById(id)) throw CheckUpException(id)
        val checkUp = checkUpsRepository.findCarCheckUpById(id)
        checkUpsRepository.deleteCarCheckUpById(id)
        return checkUp
    }

    fun getAllCheckUpsForCarPaged(carId: UUID, pageable: Pageable): Page<CarCheckUp> {
        if(!carRepository.existsCarById(carId)) throw CarIdException(carId)

        return checkUpsRepository.findAllByCarId(carId, pageable)
    }
}