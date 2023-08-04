package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.entity.ManufacturerModel
import com.example.homework.repository.CarRepository
import com.example.homework.repository.ManufacturerModelRepository
import com.example.homework.service.CarService
import com.example.homework.service.ManufacturerVerificationService
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class CarServiceTest {

    private var carRepository = mockk<CarRepository>()

    private val manufacturerModelRepository = mockk<ManufacturerModelRepository>()

    private val verificationService = mockk<ManufacturerVerificationService>()

    private var carService: CarService= CarService(carRepository, manufacturerModelRepository, verificationService)

    @Test
    fun `addCar should return true`() {
        val carDetails = ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1")
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
            date= LocalDate.now(),
            carDetails,
            productionYear = 2021,
            vin = "VIN1234")
        every { carRepository.save(any()) } returns car
        every { manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(any(), any())} returns true
        every { manufacturerModelRepository.findManufacturerModelByManufacturerAndModel(any(), any())} returns carDetails
        every { verificationService.verifyManufacturerModel(any(), any()) } returns true
        val result = carService.addCar("Manufacturer", "Model", 2021, "VIN1234")
        Assertions.assertEquals(car.id, result)
    }

    @Test
    fun `fetchDetailsByCarId should return the car details for a valid car ID`() {
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
            LocalDate.now(),
            ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1"),
            2021,
            "VIN1234",
            mutableListOf())
        every { carRepository.findCarById(any()) } returns car
        every { carRepository.findAll() } returns mutableListOf(car)
        every { carRepository.findCarById(any()) } returns car
        every {carRepository.existsCarById(any())} returns true

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val carFromDb = carService.fetchDetailsByCarId(carId)
        Assertions.assertEquals(carId, carFromDb?.id)
    }

    @Test
    fun `fetchDetailsByCarId should throw error for an invalid car ID`() {
        every { carRepository.findCarById(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")) } returns null
        every { carRepository.findAll() } returns listOf()
        every { carRepository.existsCarById(any())} returns false

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")

        Assertions.assertThrows(CarIdException::class.java) { -> carService.fetchDetailsByCarId(carId) }
    }

    @Test
    fun `isCheckUpNecessary should return true when no check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
                LocalDate.now(),
                ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1"),
                2021,
                "VIN1234",
                mutableListOf())
        )

        every { carRepository.findAll() } returns cars

        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val result = carService.isCheckUpNecessary(carId)
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `isCheckUpNecessary should return false when a check-up performed within the last year`() {
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
            LocalDate.now(),
            ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1"),
            2021,
            "VIN1234",
            mutableListOf())
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
    fun `testDeleteCar With Valid CarId Car Deleted Successfully`() {
        val carIdToDelete = UUID.randomUUID()
        val carToDelete = Car(
            id = carIdToDelete,
            date = LocalDate.now(),
            carDetails = ManufacturerModel(manufacturer = "Manufacturer1", model = "Model1"),
            productionYear = 2022,
            vin = "VIN1",
            checkUps = mutableListOf()
        )

        every { carRepository.existsCarById(carIdToDelete) } returns true
        justRun { carRepository.deleteCarById(carIdToDelete) }
        every { carRepository.findCarById(carIdToDelete)} returns carToDelete

        val deletedCar = carService.deleteCar(carIdToDelete)

        Assertions.assertEquals(carIdToDelete, deletedCar?.id)
    }

    @Test
    fun `testDeleteCar With Invalid CarId CarIdException Thrown`() {
        val invalidCarId = UUID.randomUUID()

        every { carRepository.existsCarById(invalidCarId) } returns false

        Assertions.assertThrows(CarIdException::class.java) {
            carService.deleteCar(invalidCarId)
        }
    }
}