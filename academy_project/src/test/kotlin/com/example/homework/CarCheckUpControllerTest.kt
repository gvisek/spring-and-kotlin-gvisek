package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUpRequest
import com.example.homework.entity.CarRequest
import com.example.homework.service.CarCheckUpService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

@SpringBootTest
@AutoConfigureMockMvc
class CarCheckUpControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    // Mock the CarCheckUpService
    @MockBean
    private lateinit var carCheckUpService: CarCheckUpService

    @Test
    fun testAddCar() {
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
        // Assuming the carId is 1L
        val carCheckUpRequest = CarCheckUpRequest(LocalDateTime.now(), "TestWorker", 100, 1L)
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
        val carId = 1L
        val car = Car(carId, LocalDate.now(), "Manufacturer1", "Model1", Year.of(2023), "VIN1", mutableListOf())
        Mockito.`when`(carCheckUpService.fetchDetailsByCarId(carId)).thenReturn(car)

        mockMvc.get("/cars/$carId")
            .andExpect { status { isOk() } }
    }
}
