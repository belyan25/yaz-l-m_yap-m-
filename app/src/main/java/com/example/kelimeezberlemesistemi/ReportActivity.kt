package com.example.kelimeezberlemesistemi

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // 1. XML Dosyasındaki Elemanları Bağlama
        val tvTotalWords = findViewById<TextView>(R.id.tvTotalWords)
        val tvLearnedWords = findViewById<TextView>(R.id.tvLearnedWords)
        val btnPrint = findViewById<Button>(R.id.btnPrintReport)

        // 2. Dinamik Veri Setleme Alanı
        // TODO: Projenizdeki Room Veritabanını bağladığınızda verileri buradan çekmelisiniz.
        // Örn: val totalCount = db.wordDao().getTotalWordsCount()
        val totalWordsCount = 24
        val learnedWordsCount = 8

        // Verileri arayüze yazdırıyoruz
        tvTotalWords.text = totalWordsCount.toString()
        tvLearnedWords.text = learnedWordsCount.toString()

        // 3. PDF Çıktısı Alma Buton Dinleyicisi (İster 5)
        btnPrint.setOnClickListener {
            Toast.makeText(this, "Rapor PDF olarak hazırlanıyor...", Toast.LENGTH_SHORT).show()
            exportReportAsPdf()
        }
    }

    /**
     * Ekranda görünen mevcut analiz raporu arayüzünü yakalar,
     * PDF dokümanına dönüştürür ve cihazın İndirilenler klasörüne kaydeder.
     */
    private fun exportReportAsPdf() {
        // Ekrandaki kök arayüz görünümünü yakala
        val rootView = window.decorView.rootView

        // Görünümün genişlik ve yükseklik değerleri henüz oluşmadıysa sıfır hatası almamak için kontrol
        if (rootView.width <= 0 || rootView.height <= 0) {
            Toast.makeText(this, "Arayüz henüz tamamen yüklenemedi, tekrar deneyin.", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)

        // PDF Sayfası Oluşturma
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(rootView.width, rootView.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // Ekran görüntüsünü PDF sayfasına çizdirme
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // Dosyayı Cihaz Hafızasına (Downloads klasörüne) Yazma
        val targetFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Kelime_Ezberleme_Analiz_Raporu.pdf")


        try {
            pdfDocument.writeTo(FileOutputStream(targetFile))
            Toast.makeText(this, "Rapor başarıyla indirildi:\n${targetFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "PDF kaydedilirken bir hata oluştu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}