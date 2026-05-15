package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class WordleActivity : AppCompatActivity() {

    private val targetWord = "APPLE"
    private var currentRow = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordle)

        val etGuess = findViewById<EditText>(R.id.etGuess)
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val tvResult = findViewById<TextView>(R.id.tvGameResult)

        // Satırları gruplayarak tek seferde tanımlıyoruz
        val allRows = listOf(
            listOf(findViewById<TextView>(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3), findViewById(R.id.tv4), findViewById(R.id.tv5)),
            listOf(findViewById<TextView>(R.id.tv6), findViewById(R.id.tv7), findViewById(R.id.tv8), findViewById(R.id.tv9), findViewById(R.id.tv10)),
            listOf(findViewById<TextView>(R.id.tv11), findViewById(R.id.tv12), findViewById(R.id.tv13), findViewById(R.id.tv14), findViewById(R.id.tv15)),
            listOf(findViewById<TextView>(R.id.tv16), findViewById(R.id.tv17), findViewById(R.id.tv18), findViewById(R.id.tv19), findViewById(R.id.tv20)),
            listOf(findViewById<TextView>(R.id.tv21), findViewById(R.id.tv22), findViewById(R.id.tv23), findViewById(R.id.tv24), findViewById(R.id.tv25)),
            listOf(findViewById<TextView>(R.id.tv26), findViewById(R.id.tv27), findViewById(R.id.tv28), findViewById(R.id.tv29), findViewById(R.id.tv30))
        )

        btnCheck.setOnClickListener {
            val userGuess = etGuess.text.toString().uppercase().trim()

            if (userGuess.length != 5) {
                tvResult.text = "5 harf gir! 🧐"
                return@setOnClickListener
            }

            if (currentRow < 6) {
                val currentCells = allRows[currentRow]

                for (i in 0 until 5) {
                    val charGuessed = userGuess[i]
                    val cell = currentCells[i]
                    cell.text = charGuessed.toString()

                    // Renk Mantığı
                    val color = when {
                        charGuessed == targetWord[i] -> R.drawable.box_style_green
                        targetWord.contains(charGuessed) -> R.drawable.box_style_pink
                        else -> R.drawable.box_style // Yanlışsa varsayılan kalsın
                    }
                    cell.setBackgroundResource(color)
                    cell.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                }

                if (userGuess == targetWord) {
                    tvResult.text = "TEBRİKLER! 🎉"
                    btnCheck.isEnabled = false
                } else {
                    currentRow++
                    etGuess.text.clear()
                    if (currentRow == 6) tvResult.text = "Kelime: $targetWord"
                }
            }
        }
    }
}