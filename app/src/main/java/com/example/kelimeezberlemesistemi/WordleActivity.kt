package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WordleActivity : AppCompatActivity() {

    private var targetWord = "" // Artık sabit değil, Firebase'den gelecek
    private var currentRow = 0

    private lateinit var etGuess: EditText
    private lateinit var btnCheck: Button
    private lateinit var tvResult: TextView
    private lateinit var allRows: List<List<TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "WordleW"

        etGuess = findViewById(R.id.etGuess)
        btnCheck = findViewById(R.id.btnCheck)
        tvResult = findViewById(R.id.tvGameResult)

        allRows = listOf(
            listOf(findViewById(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3), findViewById(R.id.tv4), findViewById(R.id.tv5)),
            listOf(findViewById(R.id.tv6), findViewById(R.id.tv7), findViewById(R.id.tv8), findViewById(R.id.tv9), findViewById(R.id.tv10)),
            listOf(findViewById(R.id.tv11), findViewById(R.id.tv12), findViewById(R.id.tv13), findViewById(R.id.tv14), findViewById(R.id.tv15)),
            listOf(findViewById(R.id.tv16), findViewById(R.id.tv17), findViewById(R.id.tv18), findViewById(R.id.tv19), findViewById(R.id.tv20)),
            listOf(findViewById(R.id.tv21), findViewById(R.id.tv22), findViewById(R.id.tv23), findViewById(R.id.tv24), findViewById(R.id.tv25)),
            listOf(findViewById(R.id.tv26), findViewById(R.id.tv27), findViewById(R.id.tv28), findViewById(R.id.tv29), findViewById(R.id.tv30))
        )

        // Kelime gelene kadar butonu devre dışı bırakıyoruz ki uygulama çökmesin
        btnCheck.isEnabled = false
        btnCheck.text = "Yükleniyor..."

        fetchRandomWordFromFirebase()

        btnCheck.setOnClickListener {
            val userGuess = etGuess.text.toString().uppercase().trim()

            if (userGuess.length != 5) {
                Toast.makeText(this, "Lütfen 5 harfli bir kelime girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentRow < 6) {
                val currentCells = allRows[currentRow]

                // Gerçek Wordle Algoritması Değişkenleri
                val targetChars = targetWord.toCharArray()
                val guessChars = userGuess.toCharArray()
                val cellColors = Array(5) { R.drawable.box_style } // Varsayılan renk (Gri/Boş)

                // 1. AŞAMA: Önce "Tam İsabet" olan (Yeşil) harfleri bul
                for (i in 0 until 5) {
                    if (guessChars[i] == targetChars[i]) {
                        cellColors[i] = R.drawable.box_style_green
                        targetChars[i] = '*' // Bulunan harfi yıldız yap ki 2. aşamada tekrar sayılmasın
                        guessChars[i] = '-'  // Tahmin edilen harfi de kapat
                    }
                }

                // 2. AŞAMA: Yanlış yerde ama kelimenin içinde olan (Pembe/Sarı) harfleri bul
                for (i in 0 until 5) {
                    if (guessChars[i] != '-') { // Eğer yeşil değilse
                        val charIndex = targetChars.indexOf(guessChars[i])
                        if (charIndex != -1) {
                            cellColors[i] = R.drawable.box_style_pink
                            targetChars[charIndex] = '*' // Harf kullanıldı, bir daha pembeye boyama
                        }
                    }
                }

                // 3. AŞAMA: Renkleri ve harfleri ekrandaki kutulara uygula
                for (i in 0 until 5) {
                    val cell = currentCells[i]
                    cell.text = userGuess[i].toString()
                    cell.setBackgroundResource(cellColors[i])
                    cell.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                }

                if (userGuess == targetWord) {
                    tvResult.text = "TEBRİKLER! 🎉"
                    tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    btnCheck.isEnabled = false
                } else {
                    currentRow++
                    etGuess.text.clear()
                    if (currentRow == 6) {
                        tvResult.text = "Kaybettin! Kelime: $targetWord"
                        tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                        btnCheck.isEnabled = false
                    }
                }
            }
        }
    }

    private fun fetchRandomWordFromFirebase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Kelimeler")
            .child(currentUserId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fiveLetterWords = mutableListOf<String>()

                // Tüm kelimeleri gezip sadece 5 harfli olanları ayıklıyoruz
                for (child in snapshot.children) {
                    val word = child.getValue(Word::class.java)
                    if (word != null) {
                        val engWord = word.ingilizce.trim().uppercase()
                        // Eğer boşluksuz 5 harfliyse listeye ekle
                        if (engWord.length == 5) {
                            fiveLetterWords.add(engWord)
                        }
                    }
                }

                if (fiveLetterWords.isNotEmpty()) {
                    // 5 harfli kelimeler arasından rastgele birini seç
                    targetWord = fiveLetterWords.random()
                } else {
                    // Eğer havuzda hiç 5 harfli kelime yoksa çökmemesi için joker bir kelime ata
                    targetWord = "APPLE"
                    Toast.makeText(this@WordleActivity, "Havuzunuzda 5 harfli kelime bulunamadı! Varsayılan kelime seçildi.", Toast.LENGTH_LONG).show()
                }

                // Kelime çekildiğine göre butonu aktifleştir
                btnCheck.isEnabled = true
                btnCheck.text = "KONTROL ET"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WordleActivity, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}