package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Auth'u başlat
        auth = FirebaseAuth.getInstance()

        // 1. OTOMATİK GİRİŞ: Kullanıcı daha önce giriş yaptıysa ve çıkış yapmadıysa direkt HomeActivity'ye atla
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // 2. BAŞLIK RENKLENDİRME (Catchy siyah, Words mavi)
        val tvLoginTitle = findViewById<TextView>(R.id.tvLoginTitle)
        val text = "CatchyWords"
        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#1507D7")), 6, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLoginTitle.text = spannableString

        // 3. ARAYÜZ ELEMANLARINI BAĞLAMA (Hataların çözüldüğü yer)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        // --- KAYIT OL SAYFASINA GEÇİŞ ---
        btnRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // --- ŞİFREMİ UNUTTUM SAYFASINA GEÇİŞ ---
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // --- GİRİŞ YAP BUTONU (GERÇEK FIREBASE BAĞLANTISI) ---
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen e-posta ve şifrenizi girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase ile kullanıcı giriş kontrolü
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı, ana sayfaya yönlendir
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Geri tuşuna basınca tekrar Login ekranına dönmemesi için bu sayfayı kapatıyoruz
                    } else {
                        // Giriş başarısız, hatayı göster
                        Toast.makeText(this, "Giriş başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}