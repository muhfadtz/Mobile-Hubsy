package com.example.coworkingspace

data class CoworkingSpace(
    val id: String = "",
    val name: String = "",
    val rating: Double = 0.0,  // Ubah ke Double
    val reviews: Int = 0,      // Ubah ke Int
    val location: String = "",
    val price: Int = 0,        // Ubah ke Int
    val image: String = ""
)
