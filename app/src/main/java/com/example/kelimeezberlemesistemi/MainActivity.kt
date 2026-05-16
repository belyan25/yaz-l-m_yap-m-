package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- 1. BAŞLIK LOGO AYARI (Catchy: Kalın Pembe, words: İnce İtalik Yeşil) ---
        val tvLoginTitle = findViewById<TextView>(R.id.tvLoginTitle)
        val titleText = "CatchyWords"
        val spannableString = SpannableString(titleText)

        // "Catchy" kısmını senin Wordle Pembesi yap (#E91E63)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#E91E63")),
            0, 6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // "Catchy" kısmını KALIN (Bold) yap
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0, 6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "Words" kısmını Rapor Yeşil'i yap (#8BC34A)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#8BC34A")),
            6, 11,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // "Words" kısmını İTALİK yap (Kalınlık verilmedi, ince duracak)
        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
            6, 11,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvLoginTitle.text = spannableString
        // ------------------------------------------------------------------------

        // --- 2. MODERN BİLEŞEN TANIMLAMALARI ---
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        // --- 3. GİRİŞ YAP BUTONU TIKLAMA OLAYI ---
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Giriş geçerliyse Dashboard'a (HomeActivity) yönlendir
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Geri basınca tekrar giriş sayfasına atmasın diye öldürüyoruz
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 4. ŞİFREMİ UNUTTUM SAYFASINA GEÇİŞ ---
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // --- 5. KAYIT OL SAYFASINA GEÇİŞ ---
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}