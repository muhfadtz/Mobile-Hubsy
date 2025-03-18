package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvCoworkingName: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvOverview: TextView
    private lateinit var tvPrice: TextView
    private lateinit var imgCoworking: ImageView
    private lateinit var btnBook: Button
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        firestore = FirebaseFirestore.getInstance()

        val coworkingId = intent.getStringExtra("COWORKING_ID")

        tvCoworkingName = findViewById(R.id.tvCoworkingName)
        tvRating = findViewById(R.id.tvRating)
        tvLocation = findViewById(R.id.tvLocation)
        tvOverview = findViewById(R.id.tvOverview)
        tvPrice = findViewById(R.id.tvPrice)
        imgCoworking = findViewById(R.id.imgCoworking)
        btnBook = findViewById(R.id.btnBook)
        btnBack = findViewById(R.id.btnBack)

        if (coworkingId != null) {
            loadCoworkingDetails(coworkingId)
        } else {
            Toast.makeText(this, "ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadCoworkingDetails(coworkingId: String) {
        firestore.collection("workingspace").document(coworkingId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "No Name"
                    val rating = document.getDouble("rating") ?: 0.0
                    val reviews = document.getLong("reviews") ?: 0
                    val location = document.getString("location") ?: "Unknown"
                    val price = document.getLong("price") ?: 0
                    val imageUrl = document.getString("image") ?: ""
                    val overview = document.getString("overview") ?: "No Description"

                    tvCoworkingName.text = name
                    tvRating.text = "⭐ $rating ($reviews Reviews)"
                    tvLocation.text = "📍 $location"
                    tvPrice.text = "Rp $price / Day"
                    tvOverview.text = overview

                    if (imageUrl.isNotEmpty()) {
                        Glide.with(this).load(imageUrl).into(imgCoworking)
                    }

                    btnBook.text = "💬 Book For Rp $price / Day"
                    btnBook.setOnClickListener {
                        val intent = Intent(this, CheckoutActivity::class.java)
                        intent.putExtra("COWORKING_ID", coworkingId)
                        intent.putExtra("NAME", name)
                        intent.putExtra("RATING", rating)
                        intent.putExtra("REVIEWS", reviews)
                        intent.putExtra("LOCATION", location)
                        intent.putExtra("PRICE", price)
                        intent.putExtra("IMAGE", imageUrl)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }
}