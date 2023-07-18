package com.example.homework

import com.example.homework.entity.CarIdException
import com.example.homework.service.CarCheckUpService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

class CarCheckUpServiceTest {
    private lateinit var carCheckUpService: CarCheckUpService

    @BeforeEach
    fun setup() {
        carCheckUpService = CarCheckUpService()
    }

    @Test
    fun `addCar should add a new car and return true`() {
        val result = carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        assertTrue(result)
    }

    @Test
    fun `addCarCheckUp should add a new car check-up and return true`() {
        carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        val carId = 1L
        val result = carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100, carId)
        assertTrue(result)
    }

    @Test
    fun `fetchDetailsByCarId should return the car details for a valid car ID`() {
        carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertNotNull(car)
        assertEquals(carId, car?.id)
    }

    @Test
    fun `fetchDetailsByCarId should return null for an invalid car ID`() {
        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertNull(car)
    }

    @Test
    fun `fetchManufacturerAnalytics should return the map of manufacturer to count of car check ups`() {
        carCheckUpService.addCar("Manufacturer1", "Model", Year.of(2021), "VIN1")
        carCheckUpService.addCar("Manufacturer2", "Model", Year.of(2021), "VIN2")

        carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100, 1L)
        carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 150, 1L)
        carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 200, 2L)

        val analytics = carCheckUpService.fetchManufacturerAnalytics()
        
        assertEquals(2, analytics["Manufacturer1"])
        assertEquals(1, analytics["Manufacturer2"])
    }

    @Test
    fun `isCheckUpNecessary should return true when no check-up performed within the last year`() {
        carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        val carId = 1L
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertTrue(result)
    }

    @Test
    fun `isCheckUpNecessary should return false when a check-up performed within the last year`() {
        carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        val carId = 1L
        carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100, carId)
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertFalse(result)
    }

    @Test
    fun `doesCarExist should throw an exception for an invalid car ID`() {
        val carId = 1L
        assertThrows(CarIdException::class.java) {
            carCheckUpService.doesCarExist(carId)
        }
    }
}