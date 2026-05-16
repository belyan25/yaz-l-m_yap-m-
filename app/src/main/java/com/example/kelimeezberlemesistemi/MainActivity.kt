package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. KAYIT OL BUTONU (Mavi Buton)
        val btnSignUp = findViewById<TextView>(R.id.tvSignUp)
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        } // <--- btnSignUp burada bitti!

        // 2. ŞİFREMİ UNUTTUM YAZISI
        val forgotPasswordText = findViewById<TextView>(R.id.tvForgotPassword)
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        } // <--- forgotPasswordText burada bitti!
        // MainActivity.kt içindeki giriş butonu kodun
        val btnLogin = findViewById<android.widget.Button>(R.id.btnLogin) // ID'ni kontrol et (Görselde btnSignIn gibi duruyor)

        btnLogin.setOnClickListener {
            val intent = android.content.Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Giriş yaptıktan sonra geri dönünce login ekranını bir daha görmesin
        }
    }
}