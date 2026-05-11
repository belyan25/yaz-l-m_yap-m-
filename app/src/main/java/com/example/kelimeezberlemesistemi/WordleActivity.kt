package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WordleActivity : AppCompatActivity() {

    private val targetWord = "APPLE" // Şimdilik sabit, ilerde havuzdan gelecek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordle)

        val etGuess = findViewById<EditText>(R.id.etWordGuess)
        val btnCheck = findViewById<Button>(R.id.btnCheckGuess)
        val tvResult = findViewById<TextView>(R.id.tvGameResult)

        btnCheck.setOnClickListener {
            val userGuess = etGuess.text.toString().uppercase()

            if (userGuess == targetWord) {
                tvResult.text = "TEBRİKLER! Kelimeyi bildin. 🎉"
                tvResult.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            } else {
                tvResult.text = "Yanlış tahmin, tekrar dene! ❌"
                tvResult.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
    }
}