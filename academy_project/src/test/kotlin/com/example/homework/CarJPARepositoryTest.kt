package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.ManufacturerModel
import com.example.homework.repository.CarRepository
import com.example.homework.repository.CheckUpsRepository
import com.example.homework.repository.ManufacturerModelRepository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarJPARepositoryTest @Autowired constructor(
    private val carRepository: CarRepository,
    private val checkUpsRepository: CheckUpsRepository,
    private val manufacturerModelRepository: ManufacturerModelRepository
){

    @Test
    fun testAddCar() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = 2023
        val vin = "VIN1"
        val carDetails = ManufacturerModel(id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc87"),
            manufacturer = manufacturer, model = model)

        manufacturerModelRepository.save(carDetails)

        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
            date = LocalDate.now(),
            carDetails = carDetails,
            productionYear = productionYear,
            vin = vin, checkUps = mutableListOf<CarCheckUp>())

        carRepository.save(car)

        val carFromDb = carRepository.findCarById(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"))

        Assertions.assertNotNull(carFromDb)
        Assertions.assertEquals(carDetails, carFromDb?.carDetails)
        Assertions.assertEquals(productionYear, carFromDb?.productionYear)
        Assertions.assertEquals(vin, carFromDb?.vin)
    }

    @Test
    fun testAddCarCheckUp() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = 2023
        val vin = "VIN1"
        val carDetails = ManufacturerModel(id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc87"),
            manufacturer = manufacturer, model = model)

        manufacturerModelRepository.save(carDetails)

        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"),
            date = LocalDate.now(),
            carDetails = carDetails,
            productionYear = productionYear,
            vin = vin, checkUps = mutableListOf<CarCheckUp>())

        carRepository.save(car)

        val performedAt = LocalDateTime.now()
        val worker = "TestWorker"
        val price = 100

        val carChekcUp = CarCheckUp(performedAt = performedAt, worker = worker, price = price, car = car)

        val checkUpId = checkUpsRepository.save(carChekcUp).id

        val checkUpFromDb = checkUpsRepository.findCarCheckUpById(checkUpId)

        Assertions.assertNotNull(checkUpFromDb)
        Assertions.assertEquals(performedAt.toLocalDate(), checkUpFromDb?.performedAt?.toLocalDate())
        Assertions.assertEquals(worker, checkUpFromDb?.worker)
        Assertions.assertEquals(price, checkUpFromDb?.price)
        Assertions.assertEquals(car, checkUpFromDb?.car)
    }

    @Test
    fun testGetCarById() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = 2023
        val vin = "VIN1"
        val carDetails = ManufacturerModel(id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc87"),
            manufacturer = manufacturer, model = model)

        manufacturerModelRepository.save(carDetails)

        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), date = LocalDate.now(), carDetails = carDetails, productionYear = productionYear, vin = vin, checkUps = mutableListOf<CarCheckUp>())

        carRepository.save(car)

        val carFromDb = carRepository.findCarById(car.id)
        Assertions.assertNotNull(carFromDb)
        Assertions.assertEquals(carDetails, carFromDb?.carDetails)
        Assertions.assertEquals(productionYear, carFromDb?.productionYear)
        Assertions.assertEquals(vin, carFromDb?.vin)
    }

    @Test
    fun testGetAllCars() {
        val car1Id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val manufacturer1 = "Manufacturer1"
        val model1 = "Model1"
        val productionYear1 = 2023
        val vin1 = "VIN1"
        val carDetails1 = ManufacturerModel(id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc87"),
            manufacturer = manufacturer1, model = model1)

        manufacturerModelRepository.save(carDetails1)

        val car1 = Car(car1Id, LocalDate.now(), carDetails1, productionYear1, vin1 )

        val car2Id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc84")
        val manufacturer2 = "Manufacturer2"
        val model2 = "Model2"
        val productionYear2 = 2022
        val vin2 = "VIN2"
        val carDetails2 = ManufacturerModel(id = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc88"),
            manufacturer = manufacturer2, model = model2)

        manufacturerModelRepository.save(carDetails2)

        val car2 = Car(car2Id, LocalDate.now(), carDetails2, productionYear2, vin2 )

        carRepository.save(car1)

        carRepository.save(car2)

        val cars = carRepository.findAll()

        Assertions.assertEquals(2, cars.size)

        val car1FromDb = cars.find { it.id == car1Id }
        val car2FromDb = cars.find { it.id == car2Id }

        Assertions.assertNotNull(car1)
        Assertions.assertEquals(carDetails1, car1FromDb?.carDetails)
        Assertions.assertEquals(productionYear1, car1FromDb?.productionYear)
        Assertions.assertEquals(vin1, car1FromDb?.vin)

        Assertions.assertNotNull(car2FromDb)
        Assertions.assertEquals(carDetails2, car2FromDb?.carDetails)
        Assertions.assertEquals(productionYear2, car2FromDb?.productionYear)
        Assertions.assertEquals(vin2, car2FromDb?.vin)
    }
}
