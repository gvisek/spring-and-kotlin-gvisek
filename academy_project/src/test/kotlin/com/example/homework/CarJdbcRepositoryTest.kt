package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.repository.CarsJdbcRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.Year

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarJdbcRepositoryTest @Autowired constructor(
    private val jdbcTemplate: NamedParameterJdbcTemplate
){
    private val carsJdbcRepository: CarsJdbcRepository = CarsJdbcRepository(jdbcTemplate)

    @Test
    fun testAddCar() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = Year.of(2023)
        val vin = "VIN1"

        carsJdbcRepository.addCar(manufacturer, model, productionYear, vin)

        val carFromDb = jdbcTemplate.queryForObject(
            "SELECT * FROM cars WHERE manufacturer = :manufacturer AND model = :model AND productionYear = :productionYear AND vin = :vin",
            mapOf(
                "manufacturer" to manufacturer,
                "model" to model,
                "productionYear" to productionYear.value,
                "vin" to vin
            )
        ) { rs, _ ->
            Car(
                id = rs.getLong("id"),
                date = rs.getDate("date").toLocalDate(),
                manufacturer = rs.getString("manufacturer"),
                model = rs.getString("model"),
                productionYear = Year.of(rs.getInt("productionYear")),
                vin = rs.getString("vin"),
                checkUps = mutableListOf()
            )
        }

        Assertions.assertNotNull(carFromDb)
        Assertions.assertEquals(manufacturer, carFromDb?.manufacturer)
        Assertions.assertEquals(model, carFromDb?.model)
        Assertions.assertEquals(productionYear, carFromDb?.productionYear)
        Assertions.assertEquals(vin, carFromDb?.vin)
    }

    @Test
    fun testAddCarCheckUp() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = Year.of(2023)
        val vin = "VIN1"

        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer,
                "model" to model,
                "productionYear" to productionYear.value,
                "vin" to vin
            )
        )

        val carId = carsJdbcRepository.getAllCars().first().id // Get the ID of the inserted car

        val performedAt = LocalDateTime.now()
        val worker = "TestWorker"
        val price = 100

        carsJdbcRepository.addCarCheckUp(performedAt, worker, price, carId)

        val checkUpFromDb = jdbcTemplate.queryForObject(
            "SELECT * FROM checkUps WHERE performedAt = :performedAt AND worker = :worker AND price = :price AND carId = :carId",
            mapOf(
                "performedAt" to performedAt,
                "worker" to worker,
                "price" to price,
                "carId" to carId
            )
        ) { rs, _ ->
            CarCheckUp(
                id = rs.getLong("id"),
                performedAt = rs.getTimestamp("performedAt").toLocalDateTime(),
                worker = rs.getString("worker"),
                price = rs.getInt("price"),
                carId = rs.getLong("carId")
            )
        }

        Assertions.assertNotNull(checkUpFromDb)
        Assertions.assertEquals(performedAt.toLocalDate(), checkUpFromDb?.performedAt?.toLocalDate())
        Assertions.assertEquals(worker, checkUpFromDb?.worker)
        Assertions.assertEquals(price, checkUpFromDb?.price)
        Assertions.assertEquals(carId, checkUpFromDb?.carId)
    }

    @Test
    fun testGetCarById() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = Year.of(2023)
        val vin = "VIN1"

        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer,
                "model" to model,
                "productionYear" to productionYear.value,
                "vin" to vin
            )
        )

        val carId = carsJdbcRepository.getAllCars().first().id // Get the ID of the inserted car
        val car = carsJdbcRepository.getCarById(carId)

        Assertions.assertNotNull(car)
        Assertions.assertEquals(manufacturer, car.manufacturer)
        Assertions.assertEquals(model, car.model)
        Assertions.assertEquals(productionYear, car.productionYear)
        Assertions.assertEquals(vin, car.vin)
    }

    @Test
    fun testGetAllCars() {
        val car1Id = 1L
        val manufacturer1 = "Manufacturer1"
        val model1 = "Model1"
        val productionYear1 = Year.of(2023)
        val vin1 = "VIN1"

        val car2Id = 2L
        val manufacturer2 = "Manufacturer2"
        val model2 = "Model2"
        val productionYear2 = Year.of(2022)
        val vin2 = "VIN2"

        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer1,
                "model" to model1,
                "productionYear" to productionYear1.value,
                "vin" to vin1
            )
        )

        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer2,
                "model" to model2,
                "productionYear" to productionYear2.value,
                "vin" to vin2
            )
        )

        val cars = carsJdbcRepository.getAllCars()

        Assertions.assertEquals(2, cars.size)

        val car1 = cars.find { it.id == car1Id }
        val car2 = cars.find { it.id == car2Id }

        Assertions.assertNotNull(car1)
        Assertions.assertEquals(manufacturer1, car1?.manufacturer)
        Assertions.assertEquals(model1, car1?.model)
        Assertions.assertEquals(productionYear1, car1?.productionYear)
        Assertions.assertEquals(vin1, car1?.vin)

        Assertions.assertNotNull(car2)
        Assertions.assertEquals(manufacturer2, car2?.manufacturer)
        Assertions.assertEquals(model2, car2?.model)
        Assertions.assertEquals(productionYear2, car2?.productionYear)
        Assertions.assertEquals(vin2, car2?.vin)
    }

    @Test
    fun testGetAllCheckUps() {
        val manufacturer = "Manufacturer1"
        val model = "Model1"
        val productionYear = Year.of(2023)
        val vin = "VIN1"

        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer,
                "model" to model,
                "productionYear" to productionYear.value,
                "vin" to vin
            )
        )

        val carId = carsJdbcRepository.getAllCars().first().id // Get the ID of the inserted car

        jdbcTemplate.update(
            "INSERT INTO checkUps (performedAt, worker, price, carId) VALUES (CURRENT_TIMESTAMP, 'Worker1', 100, :carId)",
            mapOf("carId" to carId)
        )

        jdbcTemplate.update(
            "INSERT INTO checkUps (performedAt, worker, price, carId) VALUES (CURRENT_TIMESTAMP, 'Worker2', 150, :carId)",
            mapOf("carId" to carId)
        )

        val checkUps = carsJdbcRepository.getAllCheckUps()

        Assertions.assertEquals(2, checkUps.size)

        val checkUp1 = checkUps.find { it.id == 2L }
        val checkUp2 = checkUps.find { it.id == 3L }

        Assertions.assertNotNull(checkUp1)
        Assertions.assertEquals(carId, checkUp1?.carId)
        Assertions.assertEquals("Worker1", checkUp1?.worker)
        Assertions.assertEquals(100, checkUp1?.price)

        Assertions.assertNotNull(checkUp2)
        Assertions.assertEquals(carId, checkUp2?.carId)
        Assertions.assertEquals("Worker2", checkUp2?.worker)
        Assertions.assertEquals(150, checkUp2?.price)
    }
}
