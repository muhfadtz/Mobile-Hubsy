package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CheckoutActivity : AppCompatActivity() {
    private val TAG = "CheckoutActivity"

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
        Log.d(TAG, "onCreate started")

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inisialisasi views
        initializeViews()

        // Ambil data dari intent
        getIntentData()

        // Set data ke UI
        setupUI()

        // Setup listeners
        setupListeners()

        Log.d(TAG, "onCreate completed")
    }

    private fun initializeViews() {
        Log.d(TAG, "Initializing views")
        backButton = findViewById(R.id.back_button)
        checkoutButton = findViewById(R.id.checkout_button)
        dayInput = findViewById(R.id.day_input)
        monthInput = findViewById(R.id.month_input)
        yearInput = findViewById(R.id.year_input)
        cafeNameTextView = findViewById(R.id.cafe_name)
        cafeLocationRatingTextView = findViewById(R.id.cafe_location_rating)
        Log.d(TAG, "Views initialized")
    }

    private fun getIntentData() {
        Log.d(TAG, "Getting intent data")
        intent.let {
            coworkingId = it.getStringExtra("COWORKING_ID")
            coworkingName = it.getStringExtra("NAME")
            coworkingRating = it.getDoubleExtra("RATING", 0.0)
            coworkingReviews = it.getLongExtra("REVIEWS", 0)
            coworkingLocation = it.getStringExtra("LOCATION")
            coworkingPrice = it.getLongExtra("PRICE", 0)
            coworkingImage = it.getStringExtra("IMAGE")
        }
        Log.d(TAG, "Retrieved data: coworkingId=$coworkingId, name=$coworkingName, location=$coworkingLocation, price=$coworkingPrice")
    }

    private fun setupUI() {
        Log.d(TAG, "Setting up UI")
        cafeNameTextView.text = coworkingName
        cafeLocationRatingTextView.text = "📍 $coworkingLocation, ${coworkingRating} ⭐ ($coworkingReviews)"
        checkoutButton.text = "Checkout - Rp $coworkingPrice"
        Log.d(TAG, "UI setup completed")
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners")
        backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            finish()
        }

        checkoutButton.setOnClickListener {
            Log.d(TAG, "Checkout button clicked")
            if (validateInputs()) {
                processCheckout()
            }
        }
        Log.d(TAG, "Listeners setup completed")
    }

    private fun validateInputs(): Boolean {
        Log.d(TAG, "Validating inputs")
        val day = dayInput.text.toString().trim()
        val month = monthInput.text.toString().trim()
        val year = yearInput.text.toString().trim()

        Log.d(TAG, "Input values: day=$day, month=$month, year=$year")

        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Log.e(TAG, "Empty date fields")
            Toast.makeText(this, "Mohon isi semua field tanggal", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val dayInt = day.toInt()
            val monthInt = month.toInt()
            val yearInt = year.toInt()

            if (dayInt < 1 || dayInt > 31 || monthInt < 1 || monthInt > 12 || yearInt < 2023) {
                Log.e(TAG, "Invalid date values: day=$dayInt, month=$monthInt, year=$yearInt")
                Toast.makeText(this, "Tanggal tidak valid", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Number format exception: ${e.message}")
            Toast.makeText(this, "Format tanggal tidak valid", Toast.LENGTH_SHORT).show()
            return false
        }

        Log.d(TAG, "Inputs validated successfully")
        return true
    }

    private fun processCheckout() {
        Log.d(TAG, "Processing checkout")
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not logged in")
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        Log.d(TAG, "Current user ID: $userId")
        val bookingDate = Timestamp.now()

        val bookingData = hashMapOf(
            "coworkingId" to coworkingId,
            "userId" to userId,
            "coworkingName" to coworkingName,
            "bookingDate" to bookingDate,
            "price" to coworkingPrice,
            "status" to "pending",
            "timestamp" to Timestamp.now()
        )

        Log.d(TAG, "Booking data prepared: $bookingData")

        Log.d(TAG, "Adding booking to Firestore")
        firestore.collection("bookings").add(bookingData)
            .addOnSuccessListener { documentReference ->
                val bookingId = documentReference.id
                Log.d(TAG, "Booking added successfully with ID: $bookingId")

                Log.d(TAG, "Preparing intent for PayConfirmActivity")
                val intent = Intent(this, PayConfirmActivity::class.java).apply {
                    putExtra("BOOKING_ID", bookingId)
                    putExtra("COWORKING_NAME", coworkingName)
                    putExtra("COWORKING_LOCATION", coworkingLocation)
                    putExtra("COWORKING_PRICE", coworkingPrice)
                }

                Log.d(TAG, "Starting PayConfirmActivity")
                startActivity(intent)
                Log.d(TAG, "Finishing CheckoutActivity")
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add booking: ${e.message}")
                Toast.makeText(this, "Gagal booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}