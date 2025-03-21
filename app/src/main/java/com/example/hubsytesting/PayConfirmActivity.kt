package com.example.hubsytesting

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.concurrent.TimeUnit

class PayConfirmActivity : AppCompatActivity() {
    private val TAG = "PayConfirmActivity"

    private lateinit var btnBack: TextView
    private lateinit var tvWorkspaceName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvTimer: TextView
    private lateinit var imgQrCode: ImageView

    private var bookingId: String? = null
    private var coworkingName: String? = null
    private var coworkingLocation: String? = null
    private var coworkingPrice: Long = 0

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        setContentView(R.layout.activity_pay_confirm)

        initializeViews()
        getIntentData()
        setupUI()
        setupListeners()
        generateQRCode()
        startCountdownTimer()

        Log.d(TAG, "onCreate completed")
    }

    private fun initializeViews() {
        Log.d(TAG, "Initializing views")
        btnBack = findViewById(R.id.btnBack)
        tvWorkspaceName = findViewById(R.id.tvWorkspaceName)
        tvAddress = findViewById(R.id.tvAddress)
        tvTimer = findViewById(R.id.tvTimer)
        imgQrCode = findViewById(R.id.imgQrCode)
        Log.d(TAG, "Views initialized")
    }

    private fun getIntentData() {
        Log.d(TAG, "Getting intent data")
        intent?.let {
            bookingId = it.getStringExtra("BOOKING_ID")
            coworkingName = it.getStringExtra("COWORKING_NAME")
            coworkingLocation = it.getStringExtra("COWORKING_LOCATION")
            coworkingPrice = it.getLongExtra("COWORKING_PRICE", 0)

            Log.d(TAG, "Retrieved data: bookingId=$bookingId, name=$coworkingName, " +
                    "location=$coworkingLocation, price=$coworkingPrice")
        } ?: run {
            Log.e(TAG, "Intent is null")
        }
    }

    private fun setupUI() {
        Log.d(TAG, "Setting up UI")
        tvWorkspaceName.text = coworkingName
        tvAddress.text = "📍 $coworkingLocation"
        Log.d(TAG, "UI setup completed")
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners")
        btnBack.setOnClickListener {
            Log.d(TAG, "Back button clicked")

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()  // Menghancurkan PayConfirmActivity
        }
        Log.d(TAG, "Listeners setup completed")
    }


    private fun generateQRCode() {
        Log.d(TAG, "Generating QR code")
        try {
            if (bookingId != null) {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix = multiFormatWriter.encode(
                    bookingId,
                    BarcodeFormat.QR_CODE,
                    200,
                    200
                )
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                imgQrCode.setImageBitmap(bitmap)
                Log.d(TAG, "QR code generated successfully")
            } else {
                Log.e(TAG, "Cannot generate QR code: bookingId is null")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating QR code: ${e.message}")
        }
    }

    private fun startCountdownTimer() {
        Log.d(TAG, "Starting countdown timer")
        // 24 jam dalam miliseconds
        val timeInMillis = 24 * 60 * 60 * 1000L

        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                val timeString = String.format("%02d : %02d : %02d", hours, minutes, seconds)
                tvTimer.text = timeString
                Log.v(TAG, "Timer update: $timeString")
            }

            override fun onFinish() {
                tvTimer.text = "00 : 00 : 00"
                Log.d(TAG, "Countdown timer finished")
            }
        }.start()

        Log.d(TAG, "Countdown timer started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        countDownTimer?.cancel()
    }
}