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

        // Arkadaşının tanımladığı butonlar ve yönlendirmeler
        val btnSignUp = findViewById<Button>(R.id.btnRegister)
        val forgotPasswordText = findViewById<TextView>(R.id.textViewForgotPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Kayıt Ol ekranına geçiş
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Şifremi Unuttum ekranına geçiş
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Giriş Yap butonu (Ana ekrana geçiş)
        btnLogin.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}