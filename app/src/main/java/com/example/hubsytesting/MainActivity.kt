package com.example.hubsytesting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hubsytesting.databinding.ActivityMainBinding
import com.example.coworkingspace.CoworkingAdapter
import com.example.coworkingspace.CoworkingSpace

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CoworkingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coworkingList = listOf(
            CoworkingSpace("Cafe Moon & Co-Working Space", "⭐ 4.5 (370)", "📍 25, Ismalia Street", "Rp200K/day", R.drawable.ic_launcher_foreground),
            CoworkingSpace("Cozy Hub", "⭐ 4.7 (250)", "📍 18, Central Street", "Rp150K/day", R.drawable.ic_launcher_foreground),
            CoworkingSpace("Creative Lounge", "⭐ 4.8 (500)", "📍 10, Downtown", "Rp180K/day", R.drawable.ic_launcher_foreground)
        )

        adapter = CoworkingAdapter(coworkingList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        // Cek apakah data masuk
        if (coworkingList.isEmpty()) {
            println("RecyclerView: Data kosong!")
        } else {
            println("RecyclerView: Data berjumlah ${coworkingList.size}")
        }
    }

}
