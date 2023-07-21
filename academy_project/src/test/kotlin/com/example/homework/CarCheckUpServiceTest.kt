package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarsRepostiory
import com.example.homework.service.CarCheckUpService
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

class CarCheckUpServiceTest {

    private var carRepository = mockk<CarsRepostiory>()

    private var carCheckUpService: CarCheckUpService= CarCheckUpService(carRepository)



    @Test
    fun `addCar should return true`() {
        justRun { carRepository.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234") }
        val result = carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        assertEquals(true, result)
    }

    @Test
    fun `addCarCheckUp should return true`() {
        justRun { carRepository.addCarCheckUp(any(), any(), any(), any()) }

        val result = carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100, 1)
        assertEquals(true, result)
    }

    @Test
    fun `fetchDetailsByCarId should return the car details for a valid car ID`() {
        every { carRepository.getCarById(1) } returns Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())

        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertEquals(carId, car?.id)
    }

    @Test
    fun `fetchDetailsByCarId should return null for an invalid car ID`() {
        every { carRepository.getCarById(1) } returns null

        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertEquals(null, car)
    }

    @Test
    fun `isCheckUpNecessary should return true when no check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())
        )

        every { carRepository.getAllCars() } returns cars

        val carId = 1L
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertEquals(true, result)
    }

    @Test
    fun `isCheckUpNecessary should return false when a check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf(CarCheckUp(1, LocalDateTime.now(), "Worker", 100, 1)))
        )

        every { carRepository.getAllCars() } returns cars

        val carId = 1L
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertEquals(false, result)
    }

    @Test
    fun `doesCarExist should throw an exception for an invalid car ID`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())
        )

        every { carRepository.getAllCars() } returns cars

        val carId = 2L
        assertThrows<CarIdException> {
            carCheckUpService.doesCarExist(carId)
        }
    }
}
