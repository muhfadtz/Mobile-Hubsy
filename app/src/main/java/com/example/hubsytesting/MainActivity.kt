package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        firestore.clearPersistence() // Hapus cache Firestore agar data fresh

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
            Log.d("UserClick", "Nama pengguna ${userName.text}")
            if (firebaseAuth.currentUser != null) {
                Toast.makeText(this, "Anda sudah login sebagai ${userName.text}", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadCoworkingSpaces() {
        firestore.collection("workingspace")
            .get()
            .addOnSuccessListener { result ->
                coworkingList.clear()
                for (document in result) {
                    // Cek apakah ada spasi tersembunyi pada keys
                    for (key in document.data.keys) {
                        Log.d("FirestoreKeys", "Key ditemukan: '$key'")
                    }

                    // Perbaikan cara membaca field "name"
                    val name = document.get("name")?.toString()?.trim() ?: "No Name"

                    Log.d("FirestoreData", "ID: ${document.id}, Name: $name")

                    val coworkingSpace = CoworkingSpace(
                        id = document.id,
                        name = name, // Gunakan hasil parsing name
                        rating = document.getDouble("rating") ?: 0.0,
                        reviews = document.getLong("reviews")?.toInt() ?: 0,
                        location = document.getString("location") ?: "Unknown",
                        price = document.getLong("price")?.toInt() ?: 0,
                        image = document.getString("image") ?: ""
                    )
                    coworkingList.add(coworkingSpace)
                }

                Log.d("FirestoreData", "Data yang diterima: $coworkingList")
                coworkingAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal memuat data: ${exception.message}", exception)
                Toast.makeText(this, "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openDetailActivity(coworkingId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("COWORKING_ID", coworkingId)
        startActivity(intent)
    }
}
