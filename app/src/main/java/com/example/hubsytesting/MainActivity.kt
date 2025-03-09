package com.example.hubsytesting

import android.os.Bundle
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
        coworkingAdapter = CoworkingAdapter(coworkingList)
        recyclerView.adapter = coworkingAdapter

        // Load Data
        loadCoworkingSpaces()
    }

    private fun loadCoworkingSpaces() {
        coworkingList.add(
            CoworkingSpace(
                name = "Cafe moon & Co-Working Space",
                rating = 4.5,
                reviews = 370,
                location = "26, Ismailia street",
                price = "200k",
                image = null // Tidak pakai drawable
            )
        )
        coworkingList.add(
            CoworkingSpace(
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
}
