package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coworkingspace.CoworkingAdapter
import com.example.coworkingspace.CoworkingSpace
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var coworkingAdapter: CoworkingAdapter
    private val coworkingList = mutableListOf<CoworkingSpace>()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Mendapatkan email pengguna yang terautentikasi
        val userEmail = firebaseAuth.currentUser?.email

        // Ambil 5 karakter pertama dari email atau tampilkan "Guest" jika tidak ada email
        val userName = findViewById<TextView>(R.id.userName)
        userName.text = userEmail?.take(5) ?: "Guest"

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

        // Cek apakah pengguna sudah login
        userName.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                // Jika pengguna sudah login, tampilkan pesan dan jangan lakukan apapun
                Toast.makeText(this, "Anda sudah login sebagai ${userName.text}", Toast.LENGTH_SHORT).show()
            } else {
                // Jika belum login, arahkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadCoworkingSpaces() {
        coworkingList.add(
            CoworkingSpace(
                id = 1,
                name = "Cafe Moon & Co-Working Space",
                rating = 4.5,
                reviews = 370,
                location = "26, Ismailia Street",
                price = "200k",
                image = null
            )
        )
        coworkingList.add(
            CoworkingSpace(
                id = 2,
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
        intent.putExtra("COWORKING_ID", coworkingId)
        startActivity(intent)
    }
}
