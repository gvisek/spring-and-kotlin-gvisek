package com.example.homework


import com.example.homework.repository.ManufacturerModelRepository
import com.example.homework.service.CarService
import com.example.homework.service.ManufacturerModelService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.springtest.MockServerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.*

@MockServerTest
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application.properties"])
class SpringBootAppTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var carService: CarService

    @Autowired
    private lateinit var manufacturerModelService: ManufacturerModelService

    @Autowired
    private lateinit var manufacturerModelRepository: ManufacturerModelRepository

    private lateinit var carId: UUID

    lateinit var mockServerClient: MockServerClient


    @BeforeEach
    fun setUp(){

        mockServerClient
            .`when`(
                HttpRequest.request()
                    .withPath("/api/v1/cars/1")
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {"cars":[{"manufacturer":"Porsche","models":["911 Turbo","Cayenne","Panamera"]},{"manufacturer":"Citroen","models":["C3","C4","C5"]},{"manufacturer":"Volkswagen","models":["Polo"]},{"manufacturer":"Hyundai","models":["i30","i20","i35","i10"]}],"Car":"1"}
                    """.trimIndent())
            )

        manufacturerModelService.getAllManufacturersAndModels()

        carId = carService.addCar("Porsche", "911 Turbo", 2023, "VIN1")

    }

    @Test
    fun `Add car check-up with valid data`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/cars/checkup")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "performedAt": "2023-07-15T10:30:00",
                    "worker": "Worker1",
                    "price": 100,
                    "carId": "$carId"
            }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `Get car details for an existing car`() {
        val currentDate = LocalDate.now()
        val carDetails = manufacturerModelRepository.findManufacturerModelByManufacturerAndModel("Porsche", "911 Turbo")
        val carDetailsJson = objectMapper.writeValueAsString(carDetails)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/cars/$carId")
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
            {
                "car": {
                    "id": $carId,
                    "date": "$currentDate",
                    "carDetails": $carDetailsJson,
                    "productionYear": 2023,
                    "vin": "VIN1",
                    "checkUps": []
                },
                "checkupNecessary": true
            }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `Get analytics data`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/analytics")
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
            {
                "Porsche" : 0
            }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `Get all cars paged`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/cars/paged")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `Get all checkUps for a car paged`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/cars/checkup/paged/$carId")
        )
            .andExpect(status().isOk)
    }
}