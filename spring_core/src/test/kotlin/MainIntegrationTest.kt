
import com.infinum.academy.springcore.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ApplicationConfiguration::class])
class CarCheckUpServiceIntegrationTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var carCheckUpService: CarCheckUpService

    @BeforeEach
    fun setUp() {
        // Clear any existing check-ups before each test
        carCheckUpService.repository.findAll().keys.forEach {
            carCheckUpService.repository.deleteById(it)
        }
    }

    @Test
    fun `isCheckUpNecessary should return true if check-up is necessary`() {
        // Create a car with a check-up older than one year
        val car = Car("Audi", "A4", "AUDI2023")
        val checkUpTime = LocalDateTime.now().minusYears(2)
        carCheckUpService.repository.insert(checkUpTime, car)

        val result = carCheckUpService.isCheckUpNecessary("AUDI2023")

        assertThat(result).isTrue()
    }

    @Test
    fun `isCheckUpNecessary should return false if check-up is not necessary`() {
        // Create a car with a recent check-up
        val car = Car("Audi", "A4", "AUDI2023")
        val checkUpTime = LocalDateTime.now().minusMonths(6)
        carCheckUpService.repository.insert(checkUpTime, car)

        val result = carCheckUpService.isCheckUpNecessary("AUDI2023")

        assertThat(result).isFalse()
    }

    @Test
    fun `getCheckUps should return all check-ups for the given car VIN`() {
        // Create a car
        val car = Car("Audi", "A4", "AUDI2023")

        // Add multiple check-ups for the car
        carCheckUpService.repository.insert(LocalDateTime.now().minusDays(1), car)
        carCheckUpService.repository.insert(LocalDateTime.now().minusDays(2), car)

        // Get the check-ups
        val checkUps = carCheckUpService.getCheckUps(car.vin)

        assertThat(checkUps).hasSize(2)
    }

    @Test
    fun `getCheckUps should throw an exception if the car with the given VIN doesn't exist`() {
        val nonExistentVIN = "NonExistentVIN"

        Assertions.assertThrows(Exception::class.java) {
            carCheckUpService.addCheckUp(nonExistentVIN)
        }
    }

    @Test
    fun `countCheckUps should return the correct count of check-ups for a manufacturer`() {
        // Create cars of different manufacturers
        val audi1 = Car("Audi", "A4", "AUDI2023")
        val audi2 = Car("Audi", "A5", "AUDI2024")
        val bmw = Car("BMW", "M4", "BMW2023")

        // Add check-ups for the cars
        carCheckUpService.repository.insert(LocalDateTime.now().minusDays(1), audi1)
        carCheckUpService.repository.insert(LocalDateTime.now().minusDays(2), audi2)
        carCheckUpService.repository.insert(LocalDateTime.now().minusDays(3), bmw)

        val count = carCheckUpService.countCheckUps("Audi")

        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `check does CarCheckUpService bean exit`(){
        assertThat(applicationContext.getBean(CarCheckUpService::class.java)).isNotNull
    }

    @Test
    fun `check does CarCheckUpRepository bean exit`(){
        assertThat(applicationContext.getBean(CarCheckUpRepository::class.java)).isNotNull
    }
}