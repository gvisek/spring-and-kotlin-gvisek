package com.example.homework.entity

class InvalidManufacturerOrModelException(manufacturer: String, model: String): RuntimeException("Invalid car manufacturer: $manufacturer or model: $model for manufacturer")
