package com.example.kelimeezberlemesistemi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

        ivAiImage.visibility = View.GONE

        btnGenerateStory.setOnClickListener {
            val w1 = etWord1.text.toString().trim().uppercase()
            val w2 = etWord2.text.toString().trim().uppercase()
            val w3 = etWord3.text.toString().trim().uppercase()
            val w4 = etWord4.text.toString().trim().uppercase()
            val w5 = etWord5.text.toString().trim().uppercase()

            if (w1.isEmpty() || w2.isEmpty() || w3.isEmpty() || w4.isEmpty() || w5.isEmpty()) {
                Toast.makeText(this, "Lütfen 5 kelimeyi de girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (checkChainLogic(w1, w2) && checkChainLogic(w2, w3) && checkChainLogic(w3, w4) && checkChainLogic(w4, w5)) {
                generateStoryAndImage(listOf(w1, w2, w3, w4, w5))
            } else {
                Toast.makeText(this, "Kelimeler zincir kuralına uymuyor! Bir kelimenin son harfi, diğerinin ilk harfi olmalı.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkChainLogic(wordA: String, wordB: String): Boolean {
        if (wordA.isEmpty() || wordB.isEmpty()) return false
        return wordA.last() == wordB.first()
    }

    private fun generateStoryAndImage(words: List<String>) {
        btnGenerateStory.isEnabled = false
        btnGenerateStory.text = "Yapay Zeka Düşünüyor..."
        tvAiStory.visibility = View.VISIBLE
        tvAiStory.text = "Hikaye ve görsel yapay zeka tarafından oluşturuluyor...\nLütfen 10-15 saniye bekleyin."
        ivAiImage.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. GEMINI İLE HİKAYE ÜRETİMİ
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = GEMINI_API_KEY
                )

                val textPrompt = "Aşağıdaki 5 İngilizce kelimeyi kullanarak Türkçe, kısa ve yaratıcı bir hikaye yaz. " +
                        "İngilizce kelimeleri hikayenin içinde orijinal haliyle ve BÜYÜK HARFLERLE kullan. " +
                        "Kelimeler: ${words.joinToString(", ")}. En fazla 4-5 cümle olsun."

                val response = generativeModel.generateContent(textPrompt)

                // 2. POLLINATIONS İLE ÜCRETSİZ VE KESİNTİSİZ RESİM ÜRETİMİ
                val rawImagePrompt = "A magical cinematic illustration of ${words[0]}, ${words[1]} and ${words[2]}, highly detailed, fantasy art"
                var generatedBitmap: Bitmap? = null

                try {
                    // Boşlukları ve özel karakterleri URL formatına çeviriyoruz
                    val encodedPrompt = URLEncoder.encode(rawImagePrompt, "UTF-8")
                    val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt"

                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    val input = connection.inputStream
                    generatedBitmap = BitmapFactory.decodeStream(input)
                    connection.disconnect()

                } catch (e: Exception) {
                    println("RESİM_HATASI: ${e.message}")
                }

                // 3. EKRANI GÜNCELLEME
                withContext(Dispatchers.Main) {
                    tvAiStory.text = response.text
                    ivAiImage.visibility = View.VISIBLE

                    if (generatedBitmap != null) {
                        ivAiImage.setImageBitmap(generatedBitmap)
                    } else {
                        ivAiImage.setImageResource(android.R.drawable.ic_dialog_alert)
                        Toast.makeText(this@StoryActivity, "Resim indirilemedi, internet bağlantını kontrol et.", Toast.LENGTH_LONG).show()
                    }

                    btnGenerateStory.isEnabled = true
                    btnGenerateStory.text = "✨ YENİDEN ÜRET"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvAiStory.text = "Hikaye üretilirken hata oluştu: ${e.message}"
                    btnGenerateStory.isEnabled = true
                    btnGenerateStory.text = "✨ TEKRAR DENE"
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}