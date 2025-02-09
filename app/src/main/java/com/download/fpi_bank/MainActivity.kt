package com.download.fpi_bank

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.roundToInt
import kotlin.time.times


class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var step = 0.001
    private var money = 0.0
    private var sound_once = false
    private var isAnimating = false
    private var isCard = false
    private var toggleDone = false
    private var clickDatabaseUpdate = 1

    private var id = "12"
    private lateinit var dbRef: DatabaseReference


    private var text = ""

    private lateinit var payButton: CardView
    private lateinit var cashText: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        payButton = findViewById(R.id.pay)
        cashText = findViewById(R.id.cash)
        val designView = findViewById<ImageView>(R.id.design)
        val mainLayout = findViewById<ConstraintLayout>(R.id.card)



        checkConnection()

        payButton.setOnClickListener {
            click()
        }


        designView.post {
            cardFit()
        }
        rotateCard()

        copy1()

//
        val sharedPref1 = getSharedPreferences("name", MODE_PRIVATE).edit()
        sharedPref1.putString("name2", text).apply()

//
        val sharedPref2 = getSharedPreferences("name", MODE_PRIVATE)
        text = sharedPref2.getString("name2", "errorText").toString()

    }

    private fun copy1() {
        val constraintLayout1 = findViewById<ConstraintLayout>(R.id.constraintLayout1)
        val constraintLayout2 = findViewById<ConstraintLayout>(R.id.constraintLayout2)

        constraintLayout1.setOnClickListener {
            val textToCopy = "+BBIEPgRC +BE0EQgQ+ +BD8EMAQ6BD4EQQRCBEwAIQ-"
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

//            Toast.makeText(this, "Содержимое скопировано", Toast.LENGTH_SHORT).show()
        }

        constraintLayout2.setOnClickListener {
            val textToCopy = id
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

//            Toast.makeText(this, "ID скопирован", Toast.LENGTH_SHORT).show()
        }
    }

    private fun click() {

        money += step
        cashText.text = "$ -${"%.3f".format(money)}"

        playMoneySound()

        if (clickDatabaseUpdate == 10){
            dbRef = FirebaseDatabase.getInstance().getReference("FPI").child("Profile").child(id)
            val hashMap = hashMapOf<String, Any>(
                "money" to (money * 1000).roundToInt() / 1000.0
            )
            dbRef.updateChildren(hashMap as Map<String, Any>)

            clickDatabaseUpdate = 1
        }
        else{
            clickDatabaseUpdate++
        }
    }

    private fun checkConnection() {
        if (isNetworkAvailable(this) == false){
            val mainLayout = findViewById<ConstraintLayout>(R.id.wifi)
            mainLayout.visibility = View.VISIBLE
        }
    }

    private fun cardFit() {
        val mainLayout = findViewById<ConstraintLayout>(R.id.card)
        val designView = findViewById<ImageView>(R.id.design)
        val width1: Int = designView.width
        val layoutParams1 = mainLayout.layoutParams
        val layoutParams2 = designView.layoutParams
        layoutParams1.height = (width1 * 0.625).roundToInt()
        layoutParams1.width = width1
        layoutParams2.height = (width1 * 0.625).roundToInt()
        layoutParams2.width = width1
        mainLayout.layoutParams = layoutParams1
        designView.layoutParams = layoutParams2
        mainLayout.requestLayout()
        designView.requestLayout()
    }

    private fun rotateCard() {
        val mainLayout = findViewById<ConstraintLayout>(R.id.card)
        val designView = findViewById<ImageView>(R.id.design)
        val constraintLayout1 = findViewById<ConstraintLayout>(R.id.constraintLayout1)
        val constraintLayout2 = findViewById<ConstraintLayout>(R.id.constraintLayout2)
        val text = findViewById<TextView>(R.id.id_text)
        val metrics = resources.displayMetrics
        mainLayout.cameraDistance = 4000 * metrics.density

        mainLayout.setOnClickListener {
            if (!isAnimating) {
                isAnimating = true
                toggleDone = false

                val animation = mainLayout.animate()
                    .rotationY(mainLayout.rotationY + 180f)
                    .setDuration(500)

                animation.setUpdateListener { animator ->
                    val animatedFraction = animator.animatedFraction
                    if (animatedFraction >= 0.5f && !toggleDone) {
                        if (!isCard) {
                            designView.visibility = View.GONE
                            constraintLayout1.visibility = View.VISIBLE
                            constraintLayout2.visibility = View.VISIBLE
                            text.visibility = View.VISIBLE

                            // Применяем поворот к новым элементам
                            constraintLayout1.rotationY = -180f
                            constraintLayout2.rotationY = -180f
                            text.rotationY = -180f

                            isCard = true
                        } else {
                            designView.visibility = View.VISIBLE
                            constraintLayout1.visibility = View.GONE
                            constraintLayout2.visibility = View.GONE
                            text.visibility = View.GONE

                            // Возвращаем поворот к 0, чтобы при следующем показе не были отзеркалены
                            constraintLayout1.rotationY = 0f
                            constraintLayout2.rotationY = 0f
                            text.rotationY = 0f

                            isCard = false
                        }
                        toggleDone = true
                    }
                }

                animation.withEndAction {
                    isAnimating = false
                    toggleDone = false
                }.start()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun playMoneySound() {
        if (!sound_once){
            mediaPlayer = MediaPlayer.create(this, R.raw.money)
            mediaPlayer?.start()
            sound_once = true
        }
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer = MediaPlayer.create(this, R.raw.money)
        }
        else{
            mediaPlayer = MediaPlayer.create(this, R.raw.money)
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }

        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}