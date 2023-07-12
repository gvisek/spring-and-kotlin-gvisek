package com.infinum.academy.springcore

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


fun main() {
    val applicationContext: ApplicationContext = AnnotationConfigApplicationContext(ApplicationConfiguration::class.java)
    val carCheckUpService: CarCheckUpService = applicationContext.getBean(CarCheckUpService::class.java)

    val audi: Car = Car("Audi", "A4", "AUDI2023")
    val bmw: Car = Car("BMW", "M4", "BMW2023")
    val mercedes: Car = Car("Mercedes", "SClass", "MERC2023")
    val porsche: Car = Car("Porsche", "911", "PORSCH2023")

    carCheckUpService.repository.insert(LocalDateTime.of(2023, 7, 6, 10, 30,0), audi)
    carCheckUpService.repository.insert(LocalDateTime.of(2021, 8, 20, 15, 33,0), audi)
    carCheckUpService.repository.insert(LocalDateTime.of(2023, 4, 20, 10, 30,0), bmw)
    carCheckUpService.repository.insert(LocalDateTime.of(2022, 1, 24, 12, 20,0), porsche)
    carCheckUpService.repository.insert(LocalDateTime.of(2021, 10, 24, 12, 20,0), porsche)

    println("Porsche needs check up:  ")
    println(carCheckUpService.isCheckUpNecessary("PORSCH2023")) //should return true

    println("Audi needs check up:  ")
    println(carCheckUpService.isCheckUpNecessary("AUDI2023")) //should return true

    println("List of all chekcUps for PORSCH2023")
    println(carCheckUpService.getCheckUps("PORSCH2023"))

    println("Audi has this many checkUps: ")
    println(carCheckUpService.countCheckUps("Audi")) //returns 2 before adding new elements

}

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
class ApplicationConfiguration {

    @Bean
    fun getResource(@Value("\${database.dbName}") path: String): Resource {
        return FileUrlResource(path)
    }

    @Bean
    fun getRepository(@Value("\${app.value.switch}") switch: Int, file: Resource, dataSource: DataSource): CarCheckUpRepository{
        return if(switch == 0){
            return InFileCarCheckUpRepository(file)
        }else
            return InMemoryCarCheckUpRepository(dataSource)
    }
}

@Component
class CarCheckUpService(val repository: CarCheckUpRepository) {
    fun isCheckUpNecessary(vin: String): Boolean{
        val checkUps = repository.findAll().values;
        val currentDate = LocalDateTime.now()
        return checkUps.none { checkUp ->
            checkUp.car.vin == vin && ChronoUnit.YEARS.between(
                checkUp.performedAt,
                currentDate
            ) < 1
        }
    }

    fun addCheckUp(vin: String): CarCheckUp{
        val currentDate = LocalDateTime.now()
        val car = repository.findAll().values.map { x -> x.car }.find{ car -> car.vin == vin}
            ?: throw Exception("Car with that vin doesn't exist!")
        repository.insert(currentDate, car)
        return repository.findById(repository.findAll().keys.max())
    }


    fun getCheckUps(vin: String): List<CarCheckUp>{
        val checkUps = repository.findAll().values.filter { checkUp -> checkUp.car.vin == vin }
        if(checkUps.isEmpty()){
            throw Exception("Car with that vin doesn't exist!")
        }
        return checkUps
    }

    fun countCheckUps(manufacturer: String): Int{
        return repository.findAll().values.count { checkUps -> checkUps.car.manufacturer == manufacturer }
    }
}

interface CarCheckUpRepository {
    fun insert(performedAt: LocalDateTime, car: Car): Long
    fun findById(id: Long): CarCheckUp
    fun deleteById(id: Long): CarCheckUp
    fun findAll(): Map<Long, CarCheckUp>
}

@Component
data class DataSource(
    @Value("\${database.dbName}") val dbName: String,
    @Value("\${database.username}") val username: String,
    @Value("\${database.password}") val password: String
)


class InFileCarCheckUpRepository(
    private val carCheckUpsFileResource: Resource
): CarCheckUpRepository {
    init {
        if (carCheckUpsFileResource.exists().not()) {
            carCheckUpsFileResource.file.createNewFile()
        }
    }
    override fun insert(performedAt: LocalDateTime, car: Car): Long {
        val file = carCheckUpsFileResource.file
        val id = (file.readLines()
            .filter { it.isNotEmpty() }.maxOfOrNull { line ->
                line.split(",").first().toLong() } ?: 0) + 1
        file.appendText("$id,${car.vin},${car.manufacturer},${car.model},$performedAt\n")
        return id
    }
    override fun findById(id: Long): CarCheckUp {
        return carCheckUpsFileResource.file.readLines()
            .filter { it.isNotEmpty() }
            .find { line -> line.split(",").first().toLong() == id }
            ?.convertToCarCheckUp()
            ?: throw CarCheckUpNotFoundException(id)
    }

    override fun deleteById(id: Long): CarCheckUp {
        val checkUpLines = carCheckUpsFileResource.file.readLines()
        var lineToDelete: String? = null
        FileOutputStream(carCheckUpsFileResource.file)
            .writer()
            .use { fileOutputWriter ->
                checkUpLines.forEach { line ->
                    if (line.split(",").first().toLong() == id) {
                        lineToDelete = line
                    } else {
                        fileOutputWriter.appendLine(line)
                    }
                }
            }
        return lineToDelete?.convertToCarCheckUp() ?: throw
        CarCheckUpNotFoundException(id)
    }
    override fun findAll(): Map<Long, CarCheckUp> {
        return carCheckUpsFileResource.file.readLines()
            .map { line -> line.convertToCarCheckUp() }
            .associateBy { it.id }
    }
    private fun String.convertToCarCheckUp(): CarCheckUp {
        val tokens = split(",")
        return CarCheckUp(
            id = tokens[0].toLong(),
            performedAt = LocalDateTime.parse(tokens[4]),
            car = Car(
                vin = tokens[1],
                manufacturer = tokens[2],
                model = tokens[3]
            )
        )
    }
}


class InMemoryCarCheckUpRepository (private val database: DataSource): CarCheckUpRepository{
    private val carCheckUpMap = mutableMapOf<Long, CarCheckUp>()

    init {
        println(database)
    }

    override fun insert(performedAt: LocalDateTime, car: Car): Long {

        val id = (carCheckUpMap.keys.maxOrNull() ?: 0) + 1
        carCheckUpMap[id] = CarCheckUp(id = id, performedAt = performedAt, car = car)
        return id
    }
    override fun findById(id: Long): CarCheckUp {
        return carCheckUpMap[id] ?: throw CarCheckUpNotFoundException(id)
    }
    override fun deleteById(id: Long): CarCheckUp {
        return carCheckUpMap.remove(id) ?: throw CarCheckUpNotFoundException(id)
    }
    override fun findAll(): Map<Long, CarCheckUp> {
        return this.carCheckUpMap.toMap()
    }
}

data class Car(
    val manufacturer: String,
    val model: String,
    val vin: String
)

data class CarCheckUp(
    val id: Long,
    val performedAt: LocalDateTime,
    val car: Car
)

class CarCheckUpNotFoundException(id: Long): RuntimeException("Car check-up ID $id not found")





