package com.example.kelimeezberlemesistemi

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportActivity : AppCompatActivity() {

    private var totalWordCount = 0
    private var learnedWordCount = 0
    private var dailyWordCount = 0

    private lateinit var tvTotalWords: TextView
    private lateinit var tvLearnedWords: TextView
    private lateinit var tvDailyWordsCount: TextView
    private lateinit var btnPrint: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Öğrenme Raporu"

        tvTotalWords = findViewById(R.id.tvTotalWords)
        tvLearnedWords = findViewById(R.id.tvLearnedWords)
        tvDailyWordsCount = findViewById(R.id.tvDailyWordsCount)
        btnPrint = findViewById(R.id.btnPrintReport)
        btnBack = findViewById(R.id.btnReportBack)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchReportData()

        btnPrint.setOnClickListener {
            if (totalWordCount == 0) {
                Toast.makeText(this, "Raporlanacak kelime bulunamadı!", Toast.LENGTH_SHORT).show()
            } else {
                generatePdfReport()
            }
        }
    }

    private fun fetchReportData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Kelimeler")
            .child(currentUserId)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalWordCount = snapshot.childrenCount.toInt()
                learnedWordCount = 0
                dailyWordCount = 0

                val todayDateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

                for (child in snapshot.children) {
                    val word = child.getValue(Word::class.java)
                    if (word != null) {
                        if (word.seviye >= 6) {
                            learnedWordCount++
                        }

                        val wordDateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(word.eklenmeTarihi))
                        if (wordDateStr == todayDateStr || word.siradakiTestTarihi > System.currentTimeMillis()) {
                            dailyWordCount++
                        }
                    }
                }

                tvTotalWords.text = totalWordCount.toString()
                tvLearnedWords.text = learnedWordCount.toString()
                tvDailyWordsCount.text = "$dailyWordCount Kelime"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReportActivity, "Veri çekme hatası: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- PDF OLUŞTURMA VE OTOMATİK AÇMA BÖLÜMÜ ---
    private fun generatePdfReport() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(350, 450, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("KELİME ÖĞRENME RAPORU", 20f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Rapor Tarihi: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())}", 20f, 80f, paint)

        canvas.drawText("Toplam Havuzdaki Kelime: $totalWordCount", 20f, 120f, paint)
        canvas.drawText("Tamamen Ezberlenen (Seviye 6): $learnedWordCount", 20f, 150f, paint)
        canvas.drawText("Bugün Çalışılan Kelime (Günlük): $dailyWordCount", 20f, 180f, paint)

        val basariOrani = if (totalWordCount > 0) (learnedWordCount * 100) / totalWordCount else 0
        paint.isFakeBoldText = true
        canvas.drawText("Genel Başarı Oranı: %$basariOrani", 20f, 220f, paint)

        pdfDocument.finishPage(page)

        val fileName = "Kelime_Raporu_${System.currentTimeMillis()}.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(this, "com.example.kelimeezberlemesistemi.provider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Cihazda PDF okuyucu bulunamadı! Dosya kaydedildi.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}