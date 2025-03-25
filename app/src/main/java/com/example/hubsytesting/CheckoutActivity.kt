package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import org.json.JSONObject

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

        return true
    }

    private fun processCheckout() {
        Log.d(TAG, "Processing checkout")
        val userId = auth.currentUser?.uid ?: run {
            Log.e(TAG, "User not logged in")
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = "ORDER-${System.currentTimeMillis()}"
        val bookingData = hashMapOf(
            "coworkingId" to coworkingId,
            "userId" to userId,
            "coworkingName" to coworkingName,
            "bookingDate" to Timestamp.now(),
            "price" to coworkingPrice,
            "status" to "pending",
            "timestamp" to Timestamp.now()
        )

        Log.d(TAG, "Saving booking to Firestore")
        firestore.collection("bookings").add(bookingData)
            .addOnSuccessListener {
                Log.d(TAG, "Booking saved, getting Midtrans Snap Token")
                requestSnapToken(orderId, userId)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save booking: ${e.message}")
                Toast.makeText(this, "Gagal booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestSnapToken(orderId: String, userId: String) {
        val url = "http://192.168.1.100:5000/midtrans/charge"
        val jsonRequest = JSONObject()
        jsonRequest.put("order_id", orderId)
        jsonRequest.put("gross_amount", coworkingPrice)
        jsonRequest.put("user_id", userId)

        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest,
            { response ->
                val snapToken = response.getString("token")
                Log.d(TAG, "Received Snap Token: $snapToken")
                startMidtransPayment(snapToken)
            },
            { error ->
                Log.e(TAG, "Failed to get Snap Token: ${error.message}")
                Toast.makeText(this, "Gagal mendapatkan token pembayaran", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    private fun startMidtransPayment(snapToken: String) {
        UiKitApi.getInstance().startPaymentUiFlow(this, snapToken, object : UiKitApi.TransactionFinishedCallback {
            override fun onTransactionFinished(result: TransactionResult) {
                Log.d(TAG, "Payment Result: ${result.status}")
                Toast.makeText(this@CheckoutActivity, "Pembayaran ${result.status}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
