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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var coworkingAdapter: CoworkingAdapter
    private val coworkingList = mutableListOf<CoworkingSpace>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userEmail = firebaseAuth.currentUser?.email
        val userName = findViewById<TextView>(R.id.userName)
        userName.text = userEmail?.take(5) ?: "Guest"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        coworkingAdapter = CoworkingAdapter(coworkingList) { coworkingId ->
            openDetailActivity(coworkingId)
        }
        recyclerView.adapter = coworkingAdapter

        loadCoworkingSpaces()

        userName.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                Toast.makeText(this, "Anda sudah login sebagai ${userName.text}", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadCoworkingSpaces() {
        firestore.collection("coworking_spaces")
            .get()
            .addOnSuccessListener { result ->
                coworkingList.clear()
                for (document in result) {
                    val coworkingSpace = CoworkingSpace(
                        id = document.id,
                        name = document.getString("name") ?: "No Name",
                        rating = document.getDouble("rating") ?: 0.0,  // Ambil sebagai Double
                        reviews = document.getLong("reviews")?.toInt() ?: 0, // Ambil sebagai Int
                        location = document.getString("location") ?: "Unknown",
                        price = document.getLong("price")?.toInt() ?: 0, // Ambil sebagai Int
                        image = document.getString("image") ?: ""
                    )
                    coworkingList.add(coworkingSpace)
                }
                coworkingAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun openDetailActivity(coworkingId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("COWORKING_ID", coworkingId)
        startActivity(intent)
    }
}
