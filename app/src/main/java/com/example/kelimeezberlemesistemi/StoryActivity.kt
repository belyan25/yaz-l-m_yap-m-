package com.example.kelimeezberlemesistemi

import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        // Üst bar ve geri tuşu ayarı
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Word Chain (LLM)"

        val tvWordsChain = findViewById<TextView>(R.id.tvWordsChain)
        val tvAiStory = findViewById<TextView>(R.id.tvAiStory)
        val btnGenerateStory = findViewById<Button>(R.id.btnGenerateStory)
        val ivAiImage = findViewById<ImageView>(R.id.ivAiImage)

        // 1. Kelime zincirini dinamik bir liste olarak tanımlıyoruz
        val wordList = listOf("Brain", "Night", "Tiger", "Robin", "Noble")
        tvWordsChain.text = "Zincir Kelimeler: " + wordList.joinToString(" ➔ ")

        // 2. Gerçek Gemini Yapay Zeka Modelini Kuruyoruz
        // "BURAYA_API_KEY_GELECEK" yazan yere Google AI Studio'dan alacağın ücretsiz anahtarı koymalısın.
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = com.example.kelimeezberlemesistemi.BuildConfig.GEMINI_API_KEY
        )

        // 3. Butona tıklandığında artık simülasyon değil, gerçek yapay zeka çalışacak
        btnGenerateStory.setOnClickListener {


            tvAiStory.text = "Yapay zeka hikayeyi oluşturuyor, lütfen bekleyin..."

            // İnternet tabanlı yapay zeka işlemleri arka planda (Coroutine) çalışmalıdır
            lifecycleScope.launch {
                try {
                    // Yapay zekaya ne yapacağını söyleyen komut (Prompt)
                    val prompt = "Bana şu İngilizce kelimelerin hem kendilerini (büyük harflerle vurgulayarak) " +
                            "hem de Türkçe anlamlarını içeren yaratıcı ve kısa bir hikaye yaz. " +
                            "Kelimeler: ${wordList.joinToString(", ")}"

                    // Gemini'den yanıtı alıyoruz
                    val response = generativeModel.generateContent(prompt)

                    // Gelen gerçek hikayeyi ekrandaki TextView'a yazdırıyoruz
                    tvAiStory.text = response.text

                    // Ödev dökümanındaki görsel isterini simüle etmek için görseli gösteriyoruz
                    ivAiImage.setImageResource(R.drawable.ic_logo_tavsan)

                    Toast.makeText(this@StoryActivity, "Hikaye başarıyla üretildi!", Toast.LENGTH_SHORT).show()



                } catch (e: Exception) {
                    e.printStackTrace()
                    tvAiStory.text = "Hata oluştu! İnternet bağlantınızı veya API anahtarınızı kontrol edin."
                    Toast.makeText(this@StoryActivity, "Hata: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}