package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CheckUpException
import com.example.homework.entity.ManufacturerModel
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import com.example.homework.service.CheckUpService
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class CheckUpServiceTest {

    private var carRepository = mockk<CarRepository>()

    private var checkUpRepository: CheckUpsRepository = mockk<CheckUpsRepository>()

    private var checkUpService: CheckUpService= CheckUpService(carRepository, checkUpRepository)

    @Test
    fun `addCarCheckUp should return true`() {
        val car = Car(date= LocalDate.now(),
            carDetails = ManufacturerModel(manufacturer = "Manufacturer", model = "Model"),
            productionYear = 2021,
            vin = "VIN1234")
        val checkUp = CarCheckUp(performedAt = LocalDateTime.now(), worker = "Worker1", price = 100, car = car)
        every { checkUpRepository.save(any()) } returns checkUp
        every { carRepository.findAll() } returns mutableListOf(car)
        every { carRepository.findCarById(any()) } returns car
        every { carRepository.existsCarById(any())} returns true

        val result = checkUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100,car.id)
        Assertions.assertEquals(checkUp.id, result)
    }

    @Test
    fun `test DeleteCarCheckUp with valid CheckUpId CheckUp Deleted Successfully`() {

        val carId = UUID.randomUUID()
        val checkUpIdToDelete = UUID.randomUUID()
        val car = Car(
            id = carId,
            date = LocalDateTime.now().toLocalDate(),
            carDetails = ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1"),
            productionYear = 2023,
            vin = "VIN1",
            checkUps = mutableListOf()
        )

        val checkUpToDelete = CarCheckUp(
            id = checkUpIdToDelete,
            performedAt = LocalDateTime.now(),
            worker = "John Doe",
            price = 100,
            car = car
        )

        every { checkUpRepository.existsById(checkUpIdToDelete) } returns true
        justRun { checkUpRepository.deleteCarCheckUpById(checkUpIdToDelete) }
        every { checkUpRepository.findCarCheckUpById(checkUpIdToDelete)} returns checkUpToDelete

        val deletedCheckUp = checkUpService.deleteCarCheckUp(checkUpIdToDelete)

        assertEquals(checkUpIdToDelete, deletedCheckUp.id)

    }

    @Test
    fun testDeleteCarCheckUp_InvalidCheckUpId_CheckUpExceptionThrown() {
        val invalidCheckUpId = UUID.randomUUID()

        every { checkUpRepository.existsById(invalidCheckUpId) } returns false

        Assertions.assertThrows(CheckUpException::class.java) {
            checkUpService.deleteCarCheckUp(invalidCheckUpId)
        }
    }
}