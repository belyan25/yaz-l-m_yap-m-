package com.example.kelimeezberlemesistemi
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Firebase Auth nesnesini başlat
        auth = FirebaseAuth.getInstance()

        // XML'deki arayüz elemanlarını Kotlin'e bağlıyoruz
        val etEmail = findViewById<EditText>(R.id.editTextUsername) // E-posta olarak kullanılacak
        val etPassword = findViewById<EditText>(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)

        // 1. GİRİŞ YAP BUTONU
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Boş alan kontrolü
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen e-posta ve şifrenizi girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase ile Giriş İşlemi
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı! MainActivity'e (veya oyun ekranına) yönlendir
                        Toast.makeText(this, "Giriş Başarılı! Hoş geldin.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)

                        // Kullanıcı geri tuşuna basarsa tekrar giriş ekranına dönmesin diye bu sayfayı kapatıyoruz
                        finish()
                    } else {
                        // Giriş başarısız,Firebase'in hata mesajını göster (Yanlış şifre vb.)
                        Toast.makeText(this, "Giriş yapılamadı: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. ŞİFREMİ UNUTTUM BUTONU
        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Şifre sıfırlama için önce yukarıya e-posta adresinizi yazın.", Toast.LENGTH_LONG).show()
            } else {
                // Firebase'den şifre sıfırlama maili gönder
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Şifre sıfırlama bağlantısı e-postanıza gönderildi.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Hata: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        // 3. KAYIT OL BUTONU
        btnRegister.setOnClickListener {
            // Kayıt ol ekranına yönlendirme
             val intent = Intent(this, SignUpActivity::class.java) // Kendi kayıt Activity adınla değiştir
            startActivity(intent)
            Toast.makeText(this, "Kayıt ekranına yönlendiriliyor...", Toast.LENGTH_SHORT).show()
        }
    }
}