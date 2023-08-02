package com.example.homework.repository

import com.example.homework.entity.Car
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CarRepository: JpaRepository<Car, UUID> {
    fun save(car: Car): Car
    override fun findAll(): List<Car>
    override fun findAll(pageable: Pageable): Page<Car>
    fun findCarById(carId: UUID): Car?
    fun existsCarById(carId: UUID): Boolean
    fun deleteCarById(carId: UUID): Car
}