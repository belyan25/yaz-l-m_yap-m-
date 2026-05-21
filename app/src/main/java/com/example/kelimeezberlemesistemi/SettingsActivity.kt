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
        setContentView(R.layout.activity_settings) // XML dosyanın adının bu olduğundan emin ol

        val btnBack = findViewById<Button>(R.id.btnSettingsBack)
        val etWordLimit = findViewById<TextInputEditText>(R.id.etWordLimit)
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)
        val btnLogout = findViewById<Button>(R.id.btnSettingsLogout)

        // 1. MEVCUT AYARI YÜKLE: Kaydedilmiş limiti getir (Daha önce kaydedilmediyse varsayılan 10 olur)
        val sharedPref = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
        val mevcutLimit = sharedPref.getInt("SoruLimiti", 10)
        etWordLimit.setText(mevcutLimit.toString())

        // 2. GERİ TUŞU
        btnBack.setOnClickListener {
            finish() // Sayfayı kapatır ve bir önceki sayfaya (HomeActivity) döner
        }

        // 3. AYARLARI KAYDETME
        btnSave.setOnClickListener {
            val limitStr = etWordLimit.text.toString().trim()

            if (limitStr.isNotEmpty()) {
                val limit = limitStr.toIntOrNull()

                if (limit != null && limit > 0) {
                    // SharedPreferences'ı aç ve yeni limiti yaz
                    val editor = sharedPref.edit()
                    editor.putInt("SoruLimiti", limit)
                    editor.apply() // Değişiklikleri kaydet

                    Toast.makeText(this, "Günlük soru limiti $limit olarak ayarlandı!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lütfen 0'dan büyük geçerli bir sayı girin!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Limit alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. ÇIKIŞ YAPMA (LOGOUT)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show()

            // Çıkış yaptıktan sonra kullanıcıyı giriş/kayıt ekranına yönlendiriyoruz
            // DİKKAT: Eğer giriş sayfanın adı farklıysa (örneğin LoginActivity), aşağıdaki MainActivity yazısını ona göre değiştir!
            val intent = Intent(this, MainActivity::class.java)
            // Geri tuşuna basıldığında tekrar uygulamanın içine girmemesi için geçmişi siliyoruz
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}