package com.example.kelimeezberlemesistemi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Button>(R.id.btnSettingsBack)
        val etWordLimit = findViewById<TextInputEditText>(R.id.etWordLimit)
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)
        val btnLogout = findViewById<Button>(R.id.btnSettingsLogout)

        // Kaydedilmiş ayarı çekiyoruz (Hocanın istediği gibi varsayılan 10)
        val sharedPreferences = getSharedPreferences("CatchyWordsPrefs", Context.MODE_PRIVATE)
        val savedLimit = sharedPreferences.getInt("word_limit", 10)

        etWordLimit.setText(savedLimit.toString())

        // --- GERİ DÖNÜŞ ---
        btnBack.setOnClickListener {
            finish()
        }

        // --- SADECE 4. STORY ZORUNLU KELİME LİMİTİ KAYDI ---
        btnSave.setOnClickListener {
            val limitText = etWordLimit.text.toString().trim()

            if (limitText.isEmpty()) {
                Toast.makeText(this, "Lütfen kelime sayısı girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = sharedPreferences.edit()
            editor.putInt("word_limit", limitText.toInt())
            editor.apply()

            Toast.makeText(this, "Yeni kelime sınırı başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // --- ÇIKIŞ YAP ---
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}