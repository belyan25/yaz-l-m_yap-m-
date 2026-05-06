package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // 1. Geri okunu aktif ediyoruz (onCreate içinde olur)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSend = findViewById<Button>(R.id.btnSendReset)
        btnSend.setOnClickListener {
            Toast.makeText(this, "Sıfırlama e-postası gönderildi!", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Sol üstteki geri okuna basınca ne olacağını söylüyoruz
    // Bu fonksiyon onCreate'in DIŞINDA ama sınıfın İÇİNDE olmalı
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}