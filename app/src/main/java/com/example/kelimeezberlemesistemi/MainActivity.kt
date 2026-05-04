package com.example.kelimeezberlemesistemi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Mavi butonu buluyoruz
        val btnSignUp = findViewById<android.widget.Button>(R.id.btnRegister)

        // Butona tıklanınca ne olacağını yazıyoruz
        btnSignUp.setOnClickListener {
            val intent = android.content.Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}