package com.example.homework.entity

import java.util.*

class CheckUpException(id: UUID) : RuntimeException("CarCheckUp with ID $id does not exist!")