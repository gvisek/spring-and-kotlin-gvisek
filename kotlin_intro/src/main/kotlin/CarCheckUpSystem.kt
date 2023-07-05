import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

object CarCheckUpSystem {
    val cars: LinkedList<Car> = LinkedList()
    val checkUps: LinkedList<CarCheckUp> = LinkedList()

    fun isCheckUpNecessary(vin: String): Boolean{
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
        val car = cars.find{ car -> car.vin == vin}
            ?: throw Exception("Car with that vin doesn't exist!")
        val newCheckUp = CarCheckUp(currentDate, car)
        checkUps.add(newCheckUp)
        return newCheckUp
    }

    fun getCheckUps(vin: String): List<CarCheckUp>{
        val checkUps = CarCheckUpSystem.checkUps.filter { checkUp -> checkUp.car.vin == vin }
        if(checkUps.isEmpty()){
            throw Exception("Car with that vin doesn't exist!")
        }
        return checkUps
    }

    fun countCheckUps(manufacturer: String): Int{
        return checkUps.count { checkUps -> checkUps.car.manufacturer == manufacturer }
    }
}