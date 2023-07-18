package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.entity.CarIdException
import com.example.homework.service.CarCheckUpService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.Year

@WebMvcTest
class CarCheckUpControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var carCheckUpService: CarCheckUpService

    @BeforeEach
    fun setUp() {
        every { carCheckUpService.addCar("Manufacturer1", "Model1", Year.of(2023), "VIN1") } answers { true }
        every { carCheckUpService.fetchDetailsByCarId(1) } answers { returnCar() }
        every { carCheckUpService.isCheckUpNecessary(1) } returns true
        every { carCheckUpService.isCheckUpNecessary(100) } returns false
        every { carCheckUpService.fetchDetailsByCarId(100) } returns null
        every { carCheckUpService.fetchManufacturerAnalytics() } returns mutableMapOf()
    }

    @Test
    fun `Add car check-up with valid data`() {
        every { carCheckUpService.addCarCheckUp(any(), any(), any(), any()) } returns true

        val requestBody = """
            {
              "performedAt": "2023-07-15T10:30:00",
              "worker": "Worker1",
              "price": 100,
              "carId": 1
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/cars/checkup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `Add car check-up with invalid car ID`() {
        every { carCheckUpService.addCarCheckUp(any(), any(), any(), any()) } throws CarIdException(1)

        val requestBody = """
            {
              "performedAt": "2023-07-15T10:30:00",
              "worker": "Worker1",
              "price": 100,
              "carId": 1
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/cars/checkup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Get car details for an existing car`() {
        val expectedCar = Car(1, LocalDate.now(), "Manufacturer1", "Model1", Year.of(2023), "VIN1", mutableListOf())
        every { carCheckUpService.fetchDetailsByCarId(1) } returns expectedCar

        val resposneBody = """
            {
                "car": {
                    "id": 1,
                    "date": "2023-07-16",
                    "manufacturer": "Manufacturer1",
                    "model": "Model1",
                    "productionYear": "2023",
                    "vin": "VIN1",
                    "checkUps": []
                },
                "checkupNecessary": true
            }
        """.trimIndent()

        mockMvc.get("/cars/1")
            .andExpect {
                status { isOk() }
                content { resposneBody }
            }
    }

    @Test
    fun `Get car details for a non-existing car`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/cars/100"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Get analytics for car manufacturers`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/analytics"))
            .andExpect(status().isOk)
    }

    private fun toJson(car: Car): String = ObjectMapper().writeValueAsString(car)

    private fun returnCar(): Car {
        return Car(1, LocalDate.now(), "Manufacturer1", "Model1", Year.of(2023), "VIN1", mutableListOf())
    }
}
