package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private var correctCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // XML bileşenlerini bağlıyoruz
        val progressQuiz = findViewById<ProgressBar>(R.id.quizProgressBar)
        val tvStatus = findViewById<TextView>(R.id.tvQuestionCount)
        val tvQuestion = findViewById<TextView>(R.id.tvQuestionWord)

        val btnOption1 = findViewById<Button>(R.id.btnOption1)
        val btnOption2 = findViewById<Button>(R.id.btnOption2)
        val btnOption3 = findViewById<Button>(R.id.btnOption3)
        val btnOption4 = findViewById<Button>(R.id.btnOption4)

        tvQuestion.text = "Apple"

        // DOĞRU CEVAP (Örnek: 2. Buton)
        btnOption2.setOnClickListener {
            correctCount++

            if (correctCount <= 6) {
                val progressValue = (correctCount * 100) / 6
                progressQuiz.progress = progressValue
                tvStatus.text = "İlerleme: $correctCount / 6"

                Toast.makeText(this, "Harika! $correctCount. tekrar tamam.", Toast.LENGTH_SHORT).show()

                if (correctCount == 6) {
                    Toast.makeText(this, "Tebrikler! Kelime ezberlendi. 🎉", Toast.LENGTH_LONG).show()
                }
            }
        }

        // YANLIŞ CEVAPLAR
        val wrongOptions = listOf(btnOption1, btnOption3, btnOption4)

        wrongOptions.forEach { button ->
            button.setOnClickListener {
                correctCount = 0
                progressQuiz.progress = 0
                tvStatus.text = "Hatalı! Başa döndün: 0 / 6"
                Toast.makeText(this, "Yanlış cevap! Sayaç sıfırlandı.", Toast.LENGTH_SHORT).show()
            }
        }
    } // onCreate burada kapanır
} // Class burada kapanır