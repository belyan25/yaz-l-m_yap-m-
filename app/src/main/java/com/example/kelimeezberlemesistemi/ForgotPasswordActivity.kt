package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    // Firebase Auth nesnesini tanımlıyoruz
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Firebase Auth nesnesini başlat
        auth = FirebaseAuth.getInstance()

        // 1. Geri okunu aktif ediyoruz
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // XML'deki elemanları Kotlin'e bağlıyoruz
        val etEmail = findViewById<EditText>(R.id.etForgotEmail)
        val btnSend = findViewById<Button>(R.id.btnSendReset)

        // Butona tıklandığında yapılacaklar
        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()

            // Güvenlik Kontrolü: E-posta alanı boş mu?
            if (email.isEmpty()) {
                Toast.makeText(this, "Lütfen kayıtlı e-posta adresinizi girin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase'den şifre sıfırlama maili gönderme komutu
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // İşlem başarılıysa kullanıcıya bilgi ver ve bu ekranı kapatıp giriş ekranına dön
                        Toast.makeText(this, "Sıfırlama bağlantısı e-postanıza gönderildi! (Spam klasörünü kontrol etmeyi unutmayın)", Toast.LENGTH_LONG).show()
                        finish() // Ekranı kapatıp geldiği yere (LoginActivity) geri döndürür
                    } else {
                        // İşlem başarısızsa (Örn: Böyle bir e-posta kayıtlı değilse) Firebase'in hatasını göster
                        Toast.makeText(this, "Hata: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // 2. Sol üstteki geri okuna basınca ne olacağını söylüyoruz
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}