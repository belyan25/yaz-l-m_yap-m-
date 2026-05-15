package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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

        // Örnek kelime zinciri (Dokümandaki gibi)
        tvWordsChain.text = "Zincir Kelimeler: Brain ➔ Night ➔ Tiger ➔ Robin ➔ Noble"

        btnGenerateStory.setOnClickListener {
            // Buraya normalde LLM API (Gemini/OpenAI) entegrasyonu gelecek.
            // Şimdilik dokümandaki örnek hikayeyi simüle ediyoruz:
            tvAiStory.text = "Zeki bir çocuk olan Brain, Night boyunca ormanda ilerlerken aniden karşısına çıkan bir Tiger'dan kaçmaya çalıştı, tam umudunu kaybedecekken bir Robin kuşu ona güvenli yolu gösterdi ve böylece robin bir Noble kahraman olarak anıldı."

            // Yapay zekanın ürettiği görseli simüle etmek için bir görsel yerleştiriyoruz
            ivAiImage.setImageResource(R.drawable.ic_logo_tavsan) // Şimdilik logoyu koysun, burayı değiştirebilirsiniz
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}