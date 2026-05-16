package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {

    private lateinit var progressQuiz: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button

    private val allWords = mutableListOf<Word>() // Şıklar için tüm kelimeler
    private val quizWords = mutableListOf<Word>() // Sadece vakti gelmiş kelimeler
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        progressQuiz = findViewById(R.id.quizProgressBar)
        tvStatus = findViewById(R.id.tvQuestionCount)
        tvQuestion = findViewById(R.id.tvQuestionWord)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)

        fetchWordsFromFirebase()
    }

    private fun fetchWordsFromFirebase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // DİKKAT: Kendi Avrupa sunucunun linkini buraya eklemeyi UNUTMA!
        val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Kelimeler")
            .child(currentUserId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allWords.clear()
                quizWords.clear()

                val currentTime = System.currentTimeMillis()

                for (child in snapshot.children) {
                    val word = child.getValue(Word::class.java)
                    if (word != null) {
                        allWords.add(word)
                        // Sadece "Sıradaki test tarihi gelmiş" ve "Tamamen ezberlenmemiş (seviye < 6)" kelimeleri teste al
                        if (word.siradakiTestTarihi <= currentTime && word.seviye < 6) {
                            quizWords.add(word)
                        }
                    }
                }

                if (allWords.size < 4) {
                    Toast.makeText(this@QuizActivity, "Test için havuzda en az 4 kelime olmalıdır!", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }

                if (quizWords.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "Bugünlük tekrar edilecek kelimeniz kalmadı. Harikasınız!", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }

                quizWords.shuffle() // Soruları karıştır
                showQuestion()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@QuizActivity, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showQuestion() {
        if (currentQuestionIndex >= quizWords.size) {
            Toast.makeText(this, "Test bitti! Kelimelerin seviyeleri güncellendi.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val currentWord = quizWords[currentQuestionIndex]
        tvQuestion.text = currentWord.ingilizce
        tvStatus.text = "Soru: ${currentQuestionIndex + 1} / ${quizWords.size} (Mevcut Seviye: ${currentWord.seviye}/6)"

        // İlerleme çubuğunu güncelle
        progressQuiz.progress = ((currentQuestionIndex) * 100) / quizWords.size

        // Şıkları oluştur (1 Doğru, 3 Rastgele Yanlış)
        val wrongAnswers = allWords.filter { it.id != currentWord.id }.shuffled().take(3).map { it.turkce }
        val options = mutableListOf(currentWord.turkce)
        options.addAll(wrongAnswers)
        options.shuffle() // Şıkların yerini karıştır

        val buttons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].setOnClickListener {
                checkAnswer(options[i], currentWord)
            }
        }
    }

    private fun checkAnswer(selectedAnswer: String, word: Word) {
        val isCorrect = (selectedAnswer == word.turkce)

        if (isCorrect) {
            word.seviye += 1
            Toast.makeText(this, "Doğru! Yeni Seviye: ${word.seviye}/6", Toast.LENGTH_SHORT).show()
        } else {
            word.seviye = 0
            Toast.makeText(this, "Yanlış! Seviye sıfırlandı.", Toast.LENGTH_SHORT).show()
        }

        // Bir sonraki tarihi hesapla
        word.siradakiTestTarihi = calculateNextDate(word.seviye)

        // Firebase'i güncelle
        updateWordInFirebase(word)

        // Sonraki soruya geç
        currentQuestionIndex++
        showQuestion()
    }

    // İsterlerdeki tarihlere göre hesaplama algoritması
    private fun calculateNextDate(seviye: Int): Long {
        val now = System.currentTimeMillis()
        val dayInMillis = 24L * 60 * 60 * 1000L

        return when (seviye) {
            0 -> now // Yanlış bilindiyse hemen/aynı gün tekrar havuzuna düşer
            1 -> now + (1 * dayInMillis)   // 1 gün sonra
            2 -> now + (7 * dayInMillis)   // 1 hafta sonra
            3 -> now + (30 * dayInMillis)  // 1 ay sonra
            4 -> now + (90 * dayInMillis)  // 3 ay sonra
            5 -> now + (180 * dayInMillis) // 6 ay sonra
            6 -> now + (365 * dayInMillis) // 1 yıl sonra (Bilinen kelime havuzu)
            else -> now
        }
    }

    private fun updateWordInFirebase(word: Word) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Kelimeler")
            .child(currentUserId)
            .child(word.id)

        databaseRef.setValue(word)
    }
}