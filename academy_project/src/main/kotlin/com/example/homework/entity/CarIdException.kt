package com.example.homework.entity

class CarIdException(id: Long): RuntimeException("Car with ID $id does not exist!")