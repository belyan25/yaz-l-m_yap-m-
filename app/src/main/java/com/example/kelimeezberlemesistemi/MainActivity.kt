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
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tvLoginTitle = findViewById<TextView>(R.id.tvLoginTitle)
        val titleText = "CatchyWords"
        val spannableString = SpannableString(titleText)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#E91E63")), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#8BC34A")), 6, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.ITALIC), 6, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvLoginTitle.text = spannableString

        val btnSignUp = findViewById<TextView>(R.id.btnRegister)
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val forgotPasswordText = findViewById<TextView>(R.id.textViewForgotPassword)
        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}