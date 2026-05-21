package com.example.kelimeezberlemesistemi


import android.os.Bundle
import android.view.View
import android.widget.Button

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope


class StoryActivity : AppCompatActivity() {

    private lateinit var etWord1: EditText
    private lateinit var etWord2: EditText
    private lateinit var etWord3: EditText
    private lateinit var etWord4: EditText
    private lateinit var etWord5: EditText
    private lateinit var btnGenerateStory: Button
    private lateinit var tvAiStory: TextView
    private lateinit var ivAiImage: ImageView

    // Sadece Gemini API anahtarı kaldı, Hugging Face tamamen silindi!
    private val GEMINI_API_KEY = "AIzaSyBlDPbuiHGbELl-StfBCQksKoolVNnMp7k"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "AI Word Chain"

        etWord1 = findViewById(R.id.etWord1)
        etWord2 = findViewById(R.id.etWord2)
        etWord3 = findViewById(R.id.etWord3)
        etWord4 = findViewById(R.id.etWord4)
        etWord5 = findViewById(R.id.etWord5)
        btnGenerateStory = findViewById(R.id.btnGenerateStory)
        tvAiStory = findViewById(R.id.tvAiStory)
        ivAiImage = findViewById(R.id.ivAiImage)



        // 2. Gerçek Gemini Yapay Zeka Modelini Kuruyoruz
        // "BURAYA_API_KEY_GELECEK" yazan yere Google AI Studio'dan alacağın ücretsiz anahtarı koymalısın.
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = com.example.kelimeezberlemesistemi.BuildConfig.GEMINI_API_KEY
        )

        // 3. Butona tıklandığında artık simülasyon değil, gerçek yapay zeka çalışacak
        btnGenerateStory.setOnClickListener {

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}