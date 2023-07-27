package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.ManufacturerModel
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import com.example.homework.service.CheckUpService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

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
}