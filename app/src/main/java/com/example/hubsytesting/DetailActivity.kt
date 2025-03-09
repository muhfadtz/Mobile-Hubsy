package com.example.hubsytesting

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Ambil ID dari Intent
        val coworkingId = intent.getIntExtra("COWORKING_ID", -1)

        val tvCoworkingName = findViewById<TextView>(R.id.tvCoworkingName)
        val btnBack = findViewById<TextView>(R.id.btnBack) // Tambahkan ID tombol back

        // Dummy Data (bisa diganti dengan database atau API)
        when (coworkingId) {
            1 -> tvCoworkingName.text = "Cafe Moon & Co-Working Space"
            2 -> tvCoworkingName.text = "Urban Hive Space"
        }

        // Fungsi kembali ke MainActivity
        btnBack.setOnClickListener {
            finish() // Menutup DetailActivity dan kembali ke MainActivity
        }
    }
}
