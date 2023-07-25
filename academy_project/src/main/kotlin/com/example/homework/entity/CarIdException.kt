package com.example.homework.entity

import java.util.*

class CarIdException(id: UUID) : RuntimeException("Car with ID $id does not exist!")
