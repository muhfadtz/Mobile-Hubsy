package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
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
        auth = FirebaseAuth.getInstance()

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
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val day = dayInput.text.toString().trim()
        val month = monthInput.text.toString().trim()
        val year = yearInput.text.toString().trim()

        val dateString = "$day/$month/$year"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date? = dateFormat.parse(dateString)
        val bookingDate = date?.let { Timestamp(it) } ?: Timestamp.now()

        val bookingData = hashMapOf(
            "coworkingId" to coworkingId,
            "userId" to userId,
            "coworkingName" to coworkingName,
            "bookingDate" to bookingDate,
            "price" to coworkingPrice,
            "status" to "pending",
            "timestamp" to Timestamp.now()
        )

        firestore.collection("bookings").add(bookingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
