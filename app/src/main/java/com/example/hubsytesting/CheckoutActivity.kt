package com.example.hubsytesting

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var backButton: TextView
    private lateinit var checkoutButton: MaterialButton
    private lateinit var dayInput: TextInputEditText
    private lateinit var monthInput: TextInputEditText
    private lateinit var yearInput: TextInputEditText
    private lateinit var cafeNameTextView: TextView
    private lateinit var cafeLocationRatingTextView: TextView

    // Data dari intent
    private var coworkingId: String? = null
    private var coworkingName: String? = null
    private var coworkingRating: Double = 0.0
    private var coworkingReviews: Long = 0
    private var coworkingLocation: String? = null
    private var coworkingPrice: Long = 0
    private var coworkingImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi views
        backButton = findViewById(R.id.back_button)
        checkoutButton = findViewById(R.id.checkout_button)
        dayInput = findViewById(R.id.day_input)
        monthInput = findViewById(R.id.month_input)
        yearInput = findViewById(R.id.year_input)
        cafeNameTextView = findViewById(R.id.cafe_name)
        cafeLocationRatingTextView = findViewById(R.id.cafe_location_rating)

        // Ambil data dari intent
        coworkingId = intent.getStringExtra("COWORKING_ID")
        coworkingName = intent.getStringExtra("NAME")
        coworkingRating = intent.getDoubleExtra("RATING", 0.0)
        coworkingReviews = intent.getLongExtra("REVIEWS", 0)
        coworkingLocation = intent.getStringExtra("LOCATION")
        coworkingPrice = intent.getLongExtra("PRICE", 0)
        coworkingImage = intent.getStringExtra("IMAGE")

        // Set data ke UI
        cafeNameTextView.text = coworkingName
        cafeLocationRatingTextView.text = "📍 $coworkingLocation, ${coworkingRating} ⭐ ($coworkingReviews)"
        checkoutButton.text = "Checkout - Rp $coworkingPrice"

        // Set tanggal hari ini sebagai nilai default
        setDefaultDate()

        // Setup listeners
        backButton.setOnClickListener {
            finish()
        }

        checkoutButton.setOnClickListener {
            if (validateInputs()) {
                processCheckout()
            }
        }
    }

    private fun setDefaultDate() {
        val calendar = Calendar.getInstance()
        dayInput.setText(calendar.get(Calendar.DAY_OF_MONTH).toString())
        monthInput.setText((calendar.get(Calendar.MONTH) + 1).toString())
        yearInput.setText(calendar.get(Calendar.YEAR).toString())
    }

    private fun validateInputs(): Boolean {
        val day = dayInput.text.toString().trim()
        val month = monthInput.text.toString().trim()
        val year = yearInput.text.toString().trim()

        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua field tanggal", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val dayInt = day.toInt()
            val monthInt = month.toInt()
            val yearInt = year.toInt()

            if (dayInt < 1 || dayInt > 31 || monthInt < 1 || monthInt > 12 || yearInt < 2023) {
                Toast.makeText(this, "Tanggal tidak valid", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Format tanggal tidak valid", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun processCheckout() {
        // Implementasi proses checkout
        val day = dayInput.text.toString().trim()
        val month = monthInput.text.toString().trim()
        val year = yearInput.text.toString().trim()
        val bookingDate = "$day/$month/$year"

        val bookingData = hashMapOf(
            "coworkingId" to coworkingId,
            "userId" to "current_user_id", // Ganti dengan user ID yang sebenarnya
            "coworkingName" to coworkingName,
            "bookingDate" to bookingDate,
            "price" to coworkingPrice,
            "status" to "pending",
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("bookings").add(bookingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}