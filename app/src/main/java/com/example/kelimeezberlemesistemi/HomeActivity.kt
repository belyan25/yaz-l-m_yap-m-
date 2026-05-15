package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.graphics.Color // RENK İÇİN EKLEDİK
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan // RENK İÇİN EKLEDİK
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // --- 1. BAŞLIK AYARI (Catchy siyah, W mavi ve italik) ---
        val tvTitle = findViewById<TextView>(R.id.tvAppTitle)
        val text = "CatchyW"
        val spannableString = SpannableString(text)

        // "Catchy" kısmını Siyah yap (0'dan 6'ya kadar)
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, 6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "W" harfini Mavi yap (6'dan 7'ye kadar)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#1507D7")),
            6, 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "W" harfini İtalik yap
        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
            6, 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvTitle.text = spannableString
        // -------------------------------------------------------

        // 2. Kelime Ekleme Butonu (Extended FAB)
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAddWord)
        fabAdd.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_add_word, null)
            bottomSheet.setContentView(view)

            val btnSave = view.findViewById<Button>(R.id.btnSaveWord)
            btnSave?.setOnClickListener {
                bottomSheet.dismiss()
                Toast.makeText(this, "Kelime havuza eklendi!", Toast.LENGTH_LONG).show()
            }
            bottomSheet.show()
        }

        // 3. Sınav Sayfasına Geçiş
        val btnGoQuiz = findViewById<Button>(R.id.btnStartQuiz)
        btnGoQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        // 4. Rapor Sayfasına Geçiş
        val btnGoReport = findViewById<Button>(R.id.btnShowReport)
        btnGoReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        // 5. Wordle Sayfasına Geçiş
        val btnGoWordle = findViewById<Button>(R.id.btnStartWordle)
        btnGoWordle.setOnClickListener {
            val intent = Intent(this, WordleActivity::class.java)
            startActivity(intent)
        }

        // 6. Örnek Kelime Listesi (RecyclerView)
        val exampleWords = listOf(
            Word("Apple", "Elma", "4/6"),
            Word("Success", "Başarı", "6/6"),
            Word("Study", "Ders Çalışmak", "2/6"),
            Word("Computer", "Bilgisayar", "0/6"),
            Word("Galaxy", "Gökada", "5/6")
        )

        val rvWords = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvWords)
        rvWords.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = WordAdapter(exampleWords)
        rvWords.adapter = adapter
    }
}