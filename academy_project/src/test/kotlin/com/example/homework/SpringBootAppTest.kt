package com.example.homework

import com.example.homework.service.CarCheckUpService
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.Year

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootAppTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var carCheckUpService: CarCheckUpService

    @BeforeEach
    fun setUp(){
        carCheckUpService.addCar("Manufacturer1", "Model1", Year.of(2023), "VIN1")
    }

    @Test
    fun `Add car check-up with valid data`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/add-carCheckUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "performedAt": "2023-07-15T10:30:00",
                    "worker": "Worker1",
                    "price": 100,
                    "carId": 1
            }
            """.trimIndent())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `Get car details for an existing car`() {
        val currentDate = LocalDate.now()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/getCar/1")
        )
            .andExpect(status().isOk)
            .andExpect(content().json("""
            {
                "car": {
                    "id": 1,
                    "date": "${currentDate.toString()}",
                    "manufacturer": "Manufacturer1",
                    "model": "Model1",
                    "productionYear": "2023",
                    "vin": "VIN1",
                    "checkUps": []
                },
                "checkupNecessary": true
            }
        """.trimIndent()))
    }

    @Test
    fun `Get analytics data`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/analytics")
        )
            .andExpect(status().isOk)
            .andExpect(content().json("""
            {
                "Manufacturer1" : 0
            }
        """.trimIndent()))
    }
}