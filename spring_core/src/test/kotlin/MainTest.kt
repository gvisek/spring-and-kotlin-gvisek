import com.infinum.academy.springcore.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


class MainTest {
    private lateinit var carCheckUpService: CarCheckUpService
    private val repositoryMock: CarCheckUpRepository = mockk<InMemoryCarCheckUpRepository>()

    @BeforeEach
    fun setup() {
        carCheckUpService = CarCheckUpService(repositoryMock)
    }

    @Test
    fun `isCheckUpNecessary should return true if check-up is necessary`() {
        // Mock repository response
        every { repositoryMock.findAll().values } returns createMockCheckUps()

        val result = carCheckUpService.isCheckUpNecessary("PORSCH2023")

        assertTrue(result)
    }

    @Test
    fun `isCheckUpNecessary should return false if check-up is not necessary`() {
        // Mock repository response
        every { repositoryMock.findAll().values } returns createMockCheckUps()

        val result = carCheckUpService.isCheckUpNecessary("AUDI2023")

        assertFalse(result)
    }

    @Test
    fun `addCheckUp should throw an exception if the car with the given VIN doesn't exist`() {

        every { repositoryMock.findAll().values } returns createMockCheckUps()

        assertThrows(Exception::class.java) {
            carCheckUpService.addCheckUp("NonExistentVIN")
        }
    }

    @Test
    fun `getCheckUps should throw an exception if the car with the given VIN doesn't exist`() {

        every { repositoryMock.findAll().values } returns createMockCheckUps()

        assertThrows(Exception::class.java) {
            carCheckUpService.getCheckUps("NonExistentVIN")
        }
    }

    @Test
    fun `countCheckUps should return the correct count of check-ups for a manufacturer`() {
        // Mock repository response
        every { repositoryMock.findAll().values } returns createMockCheckUps()

        val result = carCheckUpService.countCheckUps("Audi")

        assertEquals(2, result)
    }

    @Test
    fun `countCheckUps should return 0 if no check-ups are found for the manufacturer`() {
        // Mock repository response
        every { repositoryMock.findAll().values } returns createMockCheckUps()

        val result = carCheckUpService.countCheckUps("Toyota")

        assertEquals(0, result)
    }

    private fun createMockCheckUps(): List<CarCheckUp> {
        val audi: Car = Car("Audi", "A4", "AUDI2023")
        val bmw: Car = Car("BMW", "M4", "BMW2023")
        val mercedes: Car = Car("Mercedes", "SClass", "MERC2023")
        val porsche: Car = Car("Porsche", "911", "PORSCH2023")

        return listOf(
            CarCheckUp(1, LocalDateTime.of(2023, 7, 6, 10, 30, 0), audi),
            CarCheckUp(2, LocalDateTime.of(2021, 8, 20, 15, 33, 0), audi),
            CarCheckUp(3, LocalDateTime.of(2023, 4, 20, 10, 30, 0), bmw),
            CarCheckUp(4, LocalDateTime.of(2022, 1, 24, 12, 20, 0), porsche),
            CarCheckUp(5, LocalDateTime.of(2021, 10, 24, 12, 20, 0), porsche)
        )
    }
}
