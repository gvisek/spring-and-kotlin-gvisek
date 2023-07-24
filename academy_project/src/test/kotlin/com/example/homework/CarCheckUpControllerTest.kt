package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarCheckUpRequest
import com.example.homework.entity.CarRequest
import com.example.homework.service.CarCheckUpService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class CarCheckUpControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockkBean
    private lateinit var carCheckUpService: CarCheckUpService

    @Test
    fun testAddCar() {
        every { carCheckUpService.addCar(any(), any(), any(), any()) } returns UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")

        val carRequest = CarRequest("Manufacturer1", "Model1", Year.of(2023), "VIN1")
        mockMvc.post("/cars") {
            content = objectMapper.writeValueAsString(carRequest)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun testAddCarCheckUp() {
        every{carCheckUpService.addCarCheckUp(any(), any(), any(), any())} returns UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")

        val carCheckUpRequest = CarCheckUpRequest(LocalDateTime.now(), "TestWorker", 100, UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"))
        mockMvc.post("/cars/checkup") {
            content = objectMapper.writeValueAsString(carCheckUpRequest)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun testGetCarDetails() {
        // Assuming the carId is 1L
        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer1", "Model1", 2023, "VIN1", mutableListOf())

        every { carCheckUpService.fetchDetailsByCarId(any()) } returns car
        every { carCheckUpService.isCheckUpNecessary(any()) } returns true

        mockMvc.get("/cars/$carId")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `test getAllCheckUpsForCarPaged should return checkups with 200 status`() {
        val carId = UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83")
        val pageable = Pageable.ofSize(10).withPage(0)

        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer1", "Model1", 2023, "VIN1", mutableListOf())
        val checkUp = CarCheckUp(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc84"), LocalDateTime.now(), "Worker", 100, car)

        val checkUps = listOf(checkUp)
        val page: Page<CarCheckUp> = PageImpl(checkUps, pageable, checkUps.size.toLong())

        every { carCheckUpService.getAllCheckUpsForCarPaged(any(), any()) } returns page

        mockMvc.get("/cars/checkup/paged/{carId}", carId) {
            accept = MediaType.APPLICATION_JSON
            param("size", "10")
            param("page", "0")
        }.andExpect {
            status { isOk() }
        }
        verify { carCheckUpService.getAllCheckUpsForCarPaged(carId, pageable) }
    }

    @Test
    fun `test getAllCarsPaged should return cars with 200 status`() {
        val pageable = Pageable.ofSize(10).withPage(0)
        val car = Car(UUID.fromString("3b572be6-02e1-4b73-93b8-cb7a3648bc83"), LocalDate.now(), "Manufacturer1", "Model1", 2023, "VIN1", mutableListOf())

        val cars = listOf(car)
        val page: Page<Car> = PageImpl(cars, pageable, cars.size.toLong())

        every { carCheckUpService.getAllCarsPaged(any()) } returns page

        mockMvc.get("/cars/paged") {
            accept = MediaType.APPLICATION_JSON
            param("size", "10")
            param("page", "0")
        }.andExpect {
            status { isOk() }
        }
        verify { carCheckUpService.getAllCarsPaged(pageable) }
    }

}
