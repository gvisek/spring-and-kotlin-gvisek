package com.example.homework.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class ManufacturerModelResponse(
    @JsonProperty("cars") val cars: List<CarDetails>
)

data class CarDetails(
    @JsonProperty("manufacturer") val manufactuer : String,
    @JsonProperty("models") val models: List<String>
)