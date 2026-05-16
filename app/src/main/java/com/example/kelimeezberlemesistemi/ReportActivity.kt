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

class ReportActivity : AppCompatActivity() {

    private var totalWordCount = 0
    private var learnedWordCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Öğrenme Raporu"

        val tvTotalWords = findViewById<TextView>(R.id.tvTotalWords)
        val tvLearnedWords = findViewById<TextView>(R.id.tvLearnedWords)
        val btnPrint = findViewById<Button>(R.id.btnPrintReport)

        fetchReportData(tvTotalWords, tvLearnedWords)

        btnPrint.setOnClickListener {
            if (totalWordCount == 0) {
                Toast.makeText(this, "Raporlanacak kelime bulunamadı!", Toast.LENGTH_SHORT).show()
            } else {
                generatePdfReport()
            }
        }
    }

    private fun fetchReportData(tvTotal: TextView, tvLearned: TextView) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance("https://yazilimyapimi1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Kelimeler")
            .child(currentUserId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalWordCount = snapshot.childrenCount.toInt()
                learnedWordCount = 0

                for (child in snapshot.children) {
                    val word = child.getValue(Word::class.java)
                    if (word != null && word.seviye >= 6) {
                        learnedWordCount++
                    }
                }

                tvTotal.text = totalWordCount.toString()
                tvLearned.text = learnedWordCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReportActivity, "Veri çekme hatası: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- PDF OLUŞTURMA VE OTOMATİK AÇMA BÖLÜMÜ ---
    private fun generatePdfReport() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("KELİME ÖĞRENME RAPORU", 20f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Toplam Havuzdaki Kelime: $totalWordCount", 20f, 100f, paint)
        canvas.drawText("Tamamen Ezberlenen (Seviye 6): $learnedWordCount", 20f, 130f, paint)

        val basariOrani = if (totalWordCount > 0) (learnedWordCount * 100) / totalWordCount else 0
        canvas.drawText("Genel Başarı Oranı: %$basariOrani", 20f, 160f, paint)

        pdfDocument.finishPage(page)

        val fileName = "Kelime_Raporu_${System.currentTimeMillis()}.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            // PDF'i telefona kaydet
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close() // Dosyayı kapatıp mühürlüyoruz ki başkası okuyabilsin

            // --- OTOMATİK AÇMA KODU (FILEPROVIDER İLE) ---
            val uri = FileProvider.getUriForFile(this, "com.example.kelimeezberlemesistemi.provider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                // PDF okuyucuya bu dosyayı okuması için geçici izin veriyoruz
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            // PDF'i açmayı dene
            startActivity(intent)

        } catch (e: Exception) {
            // Eğer emülatörde veya telefonda PDF açacak BİR UYGULAMA YOKSA çökmek yerine bu mesajı verir
            Toast.makeText(this, "Cihazda PDF okuyucu bulunamadı! Dosya kaydedildi.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}