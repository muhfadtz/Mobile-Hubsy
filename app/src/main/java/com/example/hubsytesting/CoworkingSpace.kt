package com.example.coworkingspace

data class CoworkingSpace(
    val id: Int, // Tambahkan ID
    val name: String,
    val rating: Double,
    val reviews: Int,
    val location: String,
    val price: String,
    val image: String? // Bisa null jika tidak ada gambar
)
