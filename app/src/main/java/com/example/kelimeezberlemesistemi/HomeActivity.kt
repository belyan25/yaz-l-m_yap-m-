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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // --- 1. BAŞLIK AYARI ---
        val tvTitle = findViewById<TextView>(R.id.tvAppTitle)
        val text = "CatchyW"
        val spannableString = SpannableString(text)

        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#1507D7")), 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.ITALIC), 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTitle.text = spannableString

        // --- 2. KELİME EKLEME BUTONU ---
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAddWord)
        fabAdd.setOnClickListener {
            try {
                val bottomSheet = BottomSheetDialog(this@HomeActivity)
                val view = layoutInflater.inflate(R.layout.dialog_add_word, null)
                bottomSheet.setContentView(view)

                val etEngWord = view.findViewById<TextInputEditText>(R.id.etEngWord)
                val etTurWord = view.findViewById<TextInputEditText>(R.id.etTurWord)
                val etSamples = view.findViewById<TextInputEditText>(R.id.etSamples)
                val btnSave = view.findViewById<Button>(R.id.btnSaveWord)

                btnSave?.setOnClickListener {
                    val eng = etEngWord.text.toString().trim()
                    val tur = etTurWord.text.toString().trim()
                    val samples = etSamples.text.toString().trim()

                    if (eng.isEmpty() || tur.isEmpty()) {
                        Toast.makeText(this@HomeActivity, "HATA: Kelime alanları boş!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    if (currentUserId == null) {
                        Toast.makeText(this@HomeActivity, "HATA: Oturum açmış kullanıcı yok!", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Kelimeler")
                        .child(currentUserId)

                    val wordId = databaseRef.push().key

                    if (wordId != null) {
                        val wordData = hashMapOf(
                            "id" to wordId,
                            "ingilizce" to eng,
                            "turkce" to tur,
                            "ornekler" to samples,
                            "eklenmeTarihi" to System.currentTimeMillis(),
                            "seviye" to 0,
                            "siradakiTestTarihi" to System.currentTimeMillis() // Eklendiği an teste hazır olur
                        )

                        databaseRef.child(wordId).setValue(wordData)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    bottomSheet.dismiss()
                                    Toast.makeText(this@HomeActivity, "Kelime başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@HomeActivity, "FIREBASE HATASI: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
                bottomSheet.show()
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "PENCERE HATASI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }

        // --- 3. DİĞER SAYFALARA GEÇİŞ BUTONLARI ---
        findViewById<MaterialCardView>(R.id.btnStartQuiz).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnShowReport).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnStartWordle).setOnClickListener {
            startActivity(Intent(this, WordleActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnGoStory).setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }

        // --- 4. FIREBASE'DEN CANLI VERİ VE GÜNLÜK HEDEF HESAPLAMA ---
        val rvWords = findViewById<RecyclerView>(R.id.rvWords)
        rvWords.layoutManager = LinearLayoutManager(this)

        val tvDailyGoalDesc = findViewById<TextView>(R.id.tvDailyGoalDesc)
        val pbDailyGoal = findViewById<ProgressBar>(R.id.pbDailyGoal)

        val currentUserIdForList = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserIdForList != null) {

            val databaseRefList = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Kelimeler")
                .child(currentUserIdForList)

            databaseRefList.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val realWordList = mutableListOf<Word>()

                    val currentTime = System.currentTimeMillis()
                    var wordsToReviewToday = 0
                    var totalActiveWords = 0

                    for (wordSnapshot in snapshot.children) {
                        val word = wordSnapshot.getValue(Word::class.java)
                        if (word != null) {
                            realWordList.add(word)

                            // Hedef Hesaplama Mantığı
                            if (word.seviye < 6) {
                                totalActiveWords++
                                // Eğer test tarihi geldiyse veya geçmişse:
                                if (word.siradakiTestTarihi <= currentTime) {
                                    wordsToReviewToday++
                                }
                            }
                        }
                    }

                    // Günlük Hedef UI Güncellemesi
                    if (totalActiveWords == 0) {
                        tvDailyGoalDesc.text = "Havuzunda test edilecek kelime yok."
                        pbDailyGoal.progress = 0
                    } else if (wordsToReviewToday > 0) {
                        tvDailyGoalDesc.text = "6 Sefer kuralına göre $wordsToReviewToday kelime kaldı"

                        // İlerleme çubuğu yüzdesi: (Yapılan Kelime Sayısı * 100) / Toplam Aktif Kelime Sayısı
                        val progress = ((totalActiveWords - wordsToReviewToday) * 100) / totalActiveWords
                        pbDailyGoal.progress = progress
                    } else {
                        tvDailyGoalDesc.text = "Bugünkü tüm tekrarları tamamladın! 🎉"
                        pbDailyGoal.progress = 100
                    }

                    // Listeyi Yenile
                    realWordList.sortByDescending { it.eklenmeTarihi }
                    rvWords.adapter = WordAdapter(realWordList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Veri çekme hatası: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}