package com.example.homework

import com.example.homework.entity.Car
import com.example.homework.service.CarCheckUpService
import org.flywaydb.core.internal.jdbc.JdbcTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.annotation.Commit
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.Year


@SpringBootTest
@AutoConfigureMockMvc
class SpringBootAppTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate;

    @BeforeEach
    fun setUp(){
        mockMvc.perform(MockMvcRequestBuilders.post("/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                {
                    "manufacturer": "Manufacturer1",
                    "model": "Model1",
                    "productionYear": 2023,
                    "vin": "VIN1"
                }
                """.trimIndent()
            ))
            .andExpect(status().isOk)
    }

    @Test
    fun `Add car check-up with valid data`() {
        val carFromDb = jdbcTemplate.query("SELECT * FROM cars") { rs, _ ->
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

        mockMvc.perform(
            MockMvcRequestBuilders.post("/cars/checkup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "performedAt": "2023-07-15T10:30:00",
                    "worker": "Worker1",
                    "price": 100,
                    "carId": ${carFromDb[0].id}
            }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
    }
    @Test
    fun `Get car details for an existing car`() {
        val carFromDb = jdbcTemplate.query("SELECT * FROM cars") { rs, _ ->
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
        val currentDate = LocalDate.now()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/cars/${carFromDb[0].id}")
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
            {
                "car": {
                    "id": ${carFromDb[0].id},
                    "date": "$currentDate",
                    "manufacturer": "Manufacturer1",
                    "model": "Model1",
                    "productionYear": "2023",
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
        val carFromDb = jdbcTemplate.query("SELECT * FROM cars") { rs, _ ->
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

        mockMvc.perform(
            MockMvcRequestBuilders.get("/analytics")
        )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
            {
                "Manufacturer1" : 0
            }
                    """.trimIndent()
                )
            )
    }
}