package com.example.homework.repository

import com.example.homework.entity.CarCheckUp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CheckUpsRepository:JpaRepository<CarCheckUp, UUID> {
    fun save(checkUp: CarCheckUp): CarCheckUp
    override fun findAll(): List<CarCheckUp>
    fun findAllByCarId(carId: UUID, pageable: Pageable): Page<CarCheckUp>
    fun findCarCheckUpById(id : UUID): CarCheckUp
    fun deleteCarCheckUpById(id: UUID): Unit
}