package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import com.example.homework.service.CarService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class CarServiceTest {

    private var carRepository = mockk<CarRepository>()

    private var checkUpRepository: CheckUpsRepository = mockk<CheckUpsRepository>()

    private var carService: CarService= CarService(carRepository, checkUpRepository)

    @Test
    fun `addCar should return true`() {
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), date= LocalDate.now(), manufacturer = "Manufacturer", model = "Model", productionYear = 2021, vin = "VIN1234")
        every { carRepository.save(any()) } returns car
        val result = carService.addCar("Manufacturer", "Model", 2021, "VIN1234")
        Assertions.assertEquals(car.id, result)
    }

    @Test
    fun `fetchDetailsByCarId should return the car details for a valid car ID`() {
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer", "Model", 2021, "VIN1234", mutableListOf())
        every { carRepository.findCarById(any()) } returns car
        every { carRepository.findAll() } returns mutableListOf(car)
        every { carRepository.findCarById(any()) } returns car

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val carFromDb = carService.fetchDetailsByCarId(carId)
        Assertions.assertEquals(carId, carFromDb?.id)
    }

    @Test
    fun `fetchDetailsByCarId should throw error for an invalid car ID`() {
        every { carRepository.findCarById(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")) } returns null
        every { carRepository.findAll() } returns listOf()

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")

        Assertions.assertThrows(CarIdException::class.java) { -> carService.fetchDetailsByCarId(carId) }
    }

    @Test
    fun `isCheckUpNecessary should return true when no check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer", "Model", 2021, "VIN1234", mutableListOf())
        )

        every { carRepository.findAll() } returns cars

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val result = carService.isCheckUpNecessary(carId)
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `isCheckUpNecessary should return false when a check-up performed within the last year`() {
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer", "Model", 2021, "VIN1234", mutableListOf())
        car.checkUps.add(CarCheckUp(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc84"), LocalDateTime.now(), "Worker", 100, car))
        val cars = mutableListOf(
            car
        )

        every { carRepository.findAll() } returns cars

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val result = carService.isCheckUpNecessary(carId)
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `doesCarExist should throw an exception for an invalid car ID`() {
        val cars = mutableListOf(
            Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer", "Model", 2021, "VIN1234", mutableListOf())
        )

        every { carRepository.findAll() } returns cars

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc84")
        assertThrows<CarIdException> {
            carService.doesCarExist(carId)
        }
    }
}