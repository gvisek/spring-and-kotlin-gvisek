package com.example.homework.repository

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.Year

interface CarsRepostiory {
    fun addCar(manufacturer: String, model: String, productionYear: Year, vin: String)
    fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: Long)
    fun getCarById(carId: Long): Car?
    fun getAllCars(): MutableList<Car>
    fun getAllCheckUps(): MutableList<CarCheckUp>
}

@Repository
class CarsJdbcRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : CarsRepostiory {

    @Transactional
    override fun addCar(manufacturer: String, model: String, productionYear: Year, vin: String) {
        jdbcTemplate.update(
            "INSERT INTO cars (date, manufacturer, model, productionYear, vin) VALUES (CURRENT_DATE, :manufacturer, :model, :productionYear, :vin)",
            mapOf(
                "manufacturer" to manufacturer,
                "model" to model,
                "productionYear" to productionYear.value,
                "vin" to vin
            )
        )
    }

    @Transactional
    override fun addCarCheckUp(performedAt: LocalDateTime, worker: String, price: Int, carId: Long) {
        val carExistsQuery = "SELECT COUNT(*) FROM cars WHERE id = :carId"
        val carExists = jdbcTemplate.queryForObject(carExistsQuery, mapOf("carId" to carId), Int::class.java)

        if (carExists == 0) {
            throw CarIdException(carId)
        }

        jdbcTemplate.update(
            "INSERT INTO checkUps (performedAt, worker, price, carId) VALUES (:performedAt, :worker, :price, :carId)",
            mapOf(
                "performedAt" to performedAt,
                "worker" to worker,
                "price" to price,
                "carId" to carId
            )
        )
    }

    @Transactional
    override fun getCarById(carId: Long): Car {
        val carQuery = """
        SELECT * FROM cars WHERE id = :carId;
        """.trimIndent()

        val car: Car? = jdbcTemplate.queryForObject(carQuery, mutableMapOf("carId" to carId)) { rs, _ ->
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

        val checkupsQuery = """
        SELECT * FROM checkUps WHERE carId = :carId;
        """.trimIndent()

        val checkups: MutableList<CarCheckUp> = jdbcTemplate.query(checkupsQuery, mapOf("carId" to carId)) { rs, _ ->
            CarCheckUp(
                id = rs.getLong("id"),
                performedAt = rs.getTimestamp("performedAt").toLocalDateTime(),
                worker = rs.getString("worker"),
                price = rs.getInt("price"),
                carId = rs.getLong("carId")
            )
        }

        car?.checkUps = checkups

        car?.checkUps?.sortByDescending { it.performedAt }

        return car ?: throw CarIdException(carId)
    }

    @Transactional
    override fun getAllCars(): MutableList<Car> {
        val query = """
        SELECT c.*, cu.id AS checkup_id, cu.performedAt, cu.worker, cu.price
        FROM cars c
        LEFT JOIN checkUps cu ON c.id = cu.carId
        """.trimIndent()

        val carMap: MutableMap<Long, Car> = mutableMapOf()

        jdbcTemplate.query(query) { rs ->
            val carId = rs.getLong("id")
            val car = carMap.getOrPut(carId) { Car(carId, rs.getDate("date").toLocalDate(), rs.getString("manufacturer"), rs.getString("model"), Year.of(rs.getInt("productionYear")), rs.getString("vin"), mutableListOf()) }
            val checkupId = rs.getLong("checkup_id")
            if (checkupId > 0) {
                val performedAt = rs.getTimestamp("performedAt").toLocalDateTime()
                val worker = rs.getString("worker")
                val price = rs.getInt("price")
                val checkup = CarCheckUp(checkupId, performedAt, worker, price, carId)
                car.checkUps.add(checkup)
            }
        }

        return carMap.values.toMutableList()
    }

    override fun getAllCheckUps(): MutableList<CarCheckUp> {
        val checkUpList = mutableListOf<CarCheckUp>()

        val checkUpQuery = "SELECT * FROM checkUps"

        jdbcTemplate.query(checkUpQuery) { rs, _: Int ->
            val checkUp = CarCheckUp(
                id = rs.getLong("id"),
                performedAt = rs.getTimestamp("performedAt").toLocalDateTime(),
                worker = rs.getString("worker"),
                price = rs.getInt("price"),
                carId = rs.getLong("carId")
            )
            checkUpList.add(checkUp)
        }
        return checkUpList
    }
}
