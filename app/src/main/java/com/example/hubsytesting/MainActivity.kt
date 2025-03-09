package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coworkingspace.CoworkingAdapter
import com.example.coworkingspace.CoworkingSpace

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var coworkingAdapter: CoworkingAdapter
    private val coworkingList = mutableListOf<CoworkingSpace>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter dengan click listener
        coworkingAdapter = CoworkingAdapter(coworkingList) { coworkingId ->
            openDetailActivity(coworkingId)
        }
        recyclerView.adapter = coworkingAdapter

        // Load Data
        loadCoworkingSpaces()

        // Setup klik pada userName untuk pindah ke LoginActivity
        val userName = findViewById<TextView>(R.id.userName)
        userName.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadCoworkingSpaces() {
        coworkingList.add(
            CoworkingSpace(
                id = 1, // Tambahkan ID
                name = "Cafe Moon & Co-Working Space",
                rating = 4.5,
                reviews = 370,
                location = "26, Ismailia Street",
                price = "200k",
                image = null // Tidak pakai drawable
            )
        )
        coworkingList.add(
            CoworkingSpace(
                id = 2, // Tambahkan ID
                name = "Urban Hive Space",
                rating = 4.7,
                reviews = 420,
                location = "Jl. Sudirman No. 10",
                price = "250k",
                image = null
            )
        )

        coworkingAdapter.notifyDataSetChanged()
    }

    // Fungsi untuk membuka halaman DetailActivity
    private fun openDetailActivity(coworkingId: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("COWORKING_ID", coworkingId) // Kirim ID tempat kerja
        startActivity(intent)
    }
}
