import com.example.homework.entity.Car
import com.example.homework.entity.CarCheckUp
import com.example.homework.entity.CarIdException
import com.example.homework.repository.CarsRepostiory
import com.example.homework.service.CarCheckUpService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

class CarCheckUpServiceTest {

    @Mock
    private lateinit var carRepository: CarsRepostiory

    @InjectMocks
    private lateinit var carCheckUpService: CarCheckUpService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `addCar should return true`() {
        val result = carCheckUpService.addCar("Manufacturer", "Model", Year.of(2021), "VIN1234")
        assertEquals(true, result)
    }

    @Test
    fun `addCarCheckUp should return true`() {
        `when`(carRepository.getAllCars()).thenReturn(mutableListOf(Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())))

        val result = carCheckUpService.addCarCheckUp(LocalDateTime.now(), "Worker", 100, 1)
        assertEquals(true, result)
    }

    @Test
    fun `fetchDetailsByCarId should return the car details for a valid car ID`() {
        `when`(carRepository.getCarById(1)).thenReturn(Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf()))

        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertEquals(carId, car?.id)
    }

    @Test
    fun `fetchDetailsByCarId should return null for an invalid car ID`() {
        `when`(carRepository.getCarById(1)).thenReturn(null)

        val carId = 1L
        val car = carCheckUpService.fetchDetailsByCarId(carId)
        assertEquals(null, car)
    }

    @Test
    fun `isCheckUpNecessary should return true when no check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())
        )

        `when`(carRepository.getAllCars()).thenReturn(cars)

        val carId = 1L
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertEquals(true, result)
    }

    @Test
    fun `isCheckUpNecessary should return false when a check-up performed within the last year`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf(CarCheckUp(1, LocalDateTime.now(), "Worker", 100, 1)))
        )

        `when`(carRepository.getAllCars()).thenReturn(cars)

        val carId = 1L
        val result = carCheckUpService.isCheckUpNecessary(carId)
        assertEquals(false, result)
    }

    @Test
    fun `doesCarExist should throw an exception for an invalid car ID`() {
        val cars = mutableListOf(
            Car(1, LocalDate.now(), "Manufacturer", "Model", Year.of(2021), "VIN1234", mutableListOf())
        )

        `when`(carRepository.getAllCars()).thenReturn(cars)

        val carId = 2L
        assertThrows<CarIdException> {
            carCheckUpService.doesCarExist(carId)
        }
    }
}
