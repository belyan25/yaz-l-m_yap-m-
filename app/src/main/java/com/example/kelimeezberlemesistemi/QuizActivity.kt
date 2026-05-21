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
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper

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

    // Butonları döngüde kolayca yönetmek için liste halinde tutacağız
    private lateinit var buttonList: List<Button>

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

        // Buton listemizi oluşturuyoruz
        buttonList = listOf(btnOption1, btnOption2, btnOption3, btnOption4)

        fetchWordsFromFirebase()
    }

    private fun fetchWordsFromFirebase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

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

                // Ayarlardaki limiti okuyoruz (Eğer ayar yapılmadıysa varsayılan 10 alır)
                val sharedPref = getSharedPreferences("UygulamaAyarlari", Context.MODE_PRIVATE)
                val soruLimiti = sharedPref.getInt("SoruLimiti", 10)

                val limitlenmisSorular = quizWords.take(soruLimiti).toMutableList()
                quizWords.clear()
                quizWords.addAll(limitlenmisSorular)

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

        // YENİ SORUYA GEÇERKEN TÜM BUTON RENKLERİNİ ESKİSİ GİBİ SIFIRLIYORUZ
        for (btn in buttonList) {
            btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
            btn.setTextColor(Color.WHITE)
            btn.isEnabled = true // Butonları tekrar tıklanabilir yapıyoruz
        }

        val currentWord = quizWords[currentQuestionIndex]
        tvQuestion.text = currentWord.ingilizce
        tvStatus.text = "Soru: ${currentQuestionIndex + 1} / ${quizWords.size} (Mevcut Seviye: ${currentWord.seviye}/6)"

        progressQuiz.progress = ((currentQuestionIndex) * 100) / quizWords.size

        val wrongAnswers = allWords.filter { it.id != currentWord.id }.shuffled().take(3).map { it.turkce }
        val options = mutableListOf(currentWord.turkce)
        options.addAll(wrongAnswers)
        options.shuffle()

        for (i in buttonList.indices) {
            buttonList[i].text = options[i]
            buttonList[i].setOnClickListener {
                // Kullanıcı tıkladığı an diğer butonlara basıp kopya çekmesin diye hepsini kilitliyoruz
                disableAllButtons()
                // Cevabı kontrol etme fonksiyonuna tıklanan butonu da gönderiyoruz
                checkAnswer(buttonList[i], options[i], currentWord)
            }
        }
    }

    private fun checkAnswer(clickedButton: Button, selectedAnswer: String, word: Word) {
        val isCorrect = (selectedAnswer == word.turkce)

        // RENKLENDİRME SİHRİ BURADA BAŞLIYOR ✨
        if (isCorrect) {
            word.seviye += 1
            // Doğru şık tık diye Rapor yeşili (#8BC34A) yanıyor!
            clickedButton.setBackgroundColor(Color.parseColor("#8BC34A"))
            clickedButton.setTextColor(Color.WHITE)
            Toast.makeText(this, "Doğru! Yeni Seviye: ${word.seviye}/6 🎉", Toast.LENGTH_SHORT).show()
        } else {
            word.seviye = 0
            // Yanlış şık CatchyWords pembesi (#E91E63) yanıyor!
            clickedButton.setBackgroundColor(Color.parseColor("#E91E63"))
            clickedButton.setTextColor(Color.WHITE)

            // Kullanıcı yanlış bildiğinde asıl doğru olan şık hangisiyse onu da yeşil yakıp gösterelim (Jüri bayılır buna)
            for (btn in buttonList) {
                if (btn.text.toString() == word.turkce) {
                    btn.setBackgroundColor(Color.parseColor("#8BC34A"))
                    btn.setTextColor(Color.WHITE)
                }
            }
            Toast.makeText(this, "Yanlış! Seviye sıfırlandı. 🌸", Toast.LENGTH_SHORT).show()
        }

        word.siradakiTestTarihi = calculateNextDate(word.seviye)
        updateWordInFirebase(word)

        // BURASI ÇOK KRİTİK: Kullanıcı rengi gözüyle 1.5 saniye görebilsin, sonra sonraki soruya geçsin
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuestionIndex++
            showQuestion()
        }, 1500) // 1500 milisaniye = 1.5 saniye bekletme süresi
    }

    // Tıklama anında butonları kilitleme fonksiyonu
    private fun disableAllButtons() {
        for (btn in buttonList) {
            btn.isEnabled = false
        }
    }

    private fun calculateNextDate(seviye: Int): Long {
        val now = System.currentTimeMillis()
        val dayInMillis = 24L * 60 * 60 * 1000L

        return when (seviye) {
            0 -> now
            1 -> now + (1 * dayInMillis)
            2 -> now + (7 * dayInMillis)
            3 -> now + (30 * dayInMillis)
            4 -> now + (90 * dayInMillis)
            5 -> now + (180 * dayInMillis)
            6 -> now + (365 * dayInMillis)
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