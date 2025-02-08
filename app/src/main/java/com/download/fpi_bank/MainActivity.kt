package com.download.fpi_bank

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var step = 0.001
    private var sum = 0.0
    private var sound_once = false

    private lateinit var payButton: CardView
    private lateinit var cashText: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        payButton = findViewById(R.id.pay)
        cashText = findViewById(R.id.cash)

        payButton.setOnClickListener {
            playMoneySound()
        }

        rotateCard()
    }

    private fun rotateCard() {
        val mainLayout = findViewById<ImageView>(R.id.card)

        val metrics: DisplayMetrics = resources.displayMetrics
        mainLayout.cameraDistance = 4000 * metrics.density

        mainLayout.setOnClickListener {
            mainLayout.animate()
                .rotationY(mainLayout.rotationY + 180f)
                .setDuration(500)
                .start()
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
            sum += step
            cashText.text = "$ -${"%.3f".format(sum)}"
        }
        else{
            mediaPlayer = MediaPlayer.create(this, R.raw.money)
            sum += step
            cashText.text = "$ -${"%.3f".format(sum)}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}