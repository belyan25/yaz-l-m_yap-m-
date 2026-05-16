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

        // --- 1. BAŞLIK AYARI (Catchy siyah, W mavi ve italik) ---
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
            // (Sistem hatasız çalıştığı için istersen bu Toast mesajlarını silebilir veya yorum satırı yapabilirsin)
            // Toast.makeText(this@HomeActivity, "1. Butona Tıklandı!", Toast.LENGTH_SHORT).show()

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
                            "eklenmeTarihi" to System.currentTimeMillis()
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
        val btnGoQuiz = findViewById<MaterialCardView>(R.id.btnStartQuiz)
        btnGoQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        val btnGoReport = findViewById<MaterialCardView>(R.id.btnShowReport)
        btnGoReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        val btnGoWordle = findViewById<MaterialCardView>(R.id.btnStartWordle)
        btnGoWordle.setOnClickListener {
            startActivity(Intent(this, WordleActivity::class.java))
        }

        val btnGoStory = findViewById<MaterialCardView>(R.id.btnGoStory)
        btnGoStory.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }

        // --- 4. FIREBASE'DEN CANLI KELİME HAVUZUNU ÇEKME ---
        val rvWords = findViewById<RecyclerView>(R.id.rvWords)
        rvWords.layoutManager = LinearLayoutManager(this)

        val currentUserIdForList = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserIdForList != null) {

            // Aynı Avrupa sunucumuz üzerinden verileri okuyoruz
            val databaseRefList = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Kelimeler")
                .child(currentUserIdForList)

            // addValueEventListener: Veritabanında bir şey değiştiği anda listeyi otomatik günceller
            databaseRefList.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val realWordList = mutableListOf<Word>()

                    // Firebase'deki tüm kelimeleri tek tek gezip listemize ekliyoruz
                    for (wordSnapshot in snapshot.children) {
                        val word = wordSnapshot.getValue(Word::class.java)
                        if (word != null) {
                            realWordList.add(word)
                        }
                    }

                    // En son eklenen kelimelerin listenin en üstünde görünmesi için listeyi tersine çeviriyoruz
                    realWordList.sortByDescending { it.eklenmeTarihi }

                    // Oluşan gerçek listeyi Adapter'a verip ekrana yansıtıyoruz
                    val adapter = WordAdapter(realWordList)
                    rvWords.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Veri çekme hatası: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}