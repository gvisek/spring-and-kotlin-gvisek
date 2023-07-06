import java.time.LocalDateTime

fun main(){

    //creating 4 cars
    val audi: Car = Car("Audi", "A4", "AUDI2023")
    val bmw: Car = Car("BMW", "M4", "BMW2023")
    val mercedes: Car = Car("Mercedes", "SClass", "MERC2023")
    val porsche: Car = Car("Porsche", "911", "PORSCH2023")

    //adding 4 cars
    CarCheckUpSystem.cars.addAll(arrayOf(audi, bmw, mercedes, porsche))

    //adding 10 CarCheckUps
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2023, 7, 6, 10, 30,0), audi))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2021, 8, 20, 15, 33,0), audi))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2019, 7, 6, 10, 30,0), audi))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2023, 4, 20, 10, 30,0), bmw))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2022, 4, 21, 10, 30,0), bmw))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2023, 11, 10, 5, 30,0), mercedes))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2022, 1, 7, 10, 30,0), mercedes))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2022, 1, 24, 12, 20,0), porsche))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2021, 10, 24, 12, 20,0), porsche))
    CarCheckUpSystem.checkUps.add(CarCheckUp(LocalDateTime.of(2020, 10, 24, 12, 20,0), porsche))

    println("Mercedes needs check up:  ")
    println(CarCheckUpSystem.isCheckUpNecessary("MERC2023")) //should return false

    println("Porsche needs check up:  ")
    println(CarCheckUpSystem.isCheckUpNecessary("PORSCH2023")) //should return true

    println("List of all chekcUps for PORSCH2023")
    println(CarCheckUpSystem.getCheckUps("PORSCH2023"))

    println("Mercedes has this many checkUps: ")
    println(CarCheckUpSystem.countCheckUps("Mercedes")) //returns 2 before adding new elements

    CarCheckUpSystem.addCheckUp("MERC2023")

    println("Mercedes has this many checkUps: ")
    println(CarCheckUpSystem.countCheckUps("Mercedes")) //returns 3 after new checkUp was added

    //println(CarCheckUpSystem.getCheckUps("PORSCH2024")) //throws an error

    //println(CarCheckUpSystem.addCheckUp("MERC2024")) //throws an error
}