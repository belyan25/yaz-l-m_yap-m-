package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    // Firebase Auth sınıfını tanımlıyoruz
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // XML dosyanın adının "activity_register" olduğunu varsayıyorum
        setContentView(R.layout.activity_sign_up)

        // Firebase Auth nesnesini başlat
        auth = FirebaseAuth.getInstance()

        // XML'deki arayüz elemanlarını Kotlin'e bağlıyoruz

        val etEmail = findViewById<EditText>(R.id.editTextEmail)
        val etPassword = findViewById<EditText>(R.id.editTextPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // KAYIT OL BUTONU TIKLANMA OLAYI
        btnRegister.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // 1. Güvenlik Kontrolü: Boş Alan Var mı?
            if ( email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Güvenlik Kontrolü: Şifreler Eşleşiyor mu?
            if (password != confirmPassword) {
                Toast.makeText(this, "Girdiğiniz şifreler birbiriyle eşleşmiyor!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Güvenlik Kontrolü: Şifre Uzunluğu (Firebase en az 6 karakter ister)
            if (password.length < 6) {
                Toast.makeText(this, "Şifreniz en az 6 karakterden oluşmalıdır!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Firebase ile Kayıt İşlemi
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Kayıt başarılı oldu! Şimdi Kullanıcı Adını profile işleyelim.
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()

                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener {


                            // Kayıt işlemi bittikten sonra Giriş Ekranına (LoginActivity) yönlendir
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                            // Kullanıcı geri tuşuna basarsa tekrar kayıt ekranına dönmesin
                            finish()
                        }
                    } else {
                        // Kayıt başarısız oldu (Örneğin e-posta formatı yanlış veya daha önce kayıt olunmuş)
                        Toast.makeText(this, "Kayıt başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}