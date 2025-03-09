package com.example.coworkingspace

data class CoworkingSpace(
    val name: String,
    val rating: Double,
    val reviews: Int,
    val location: String,
    val price: String,
    val image: Int? // Nullable, karena tidak pakai drawable
)
