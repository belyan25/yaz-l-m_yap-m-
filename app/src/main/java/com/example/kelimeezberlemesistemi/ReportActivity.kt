package com.example.kelimeezberlemesistemi

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val btnPrint = findViewById<Button>(R.id.btnPrintReport)

        btnPrint.setOnClickListener {
            // Hocanın istediği PDF/Çıktı özelliği için temel mesaj
            Toast.makeText(this, "Rapor PDF olarak hazırlanıyor...", Toast.LENGTH_SHORT).show()
        }
    }
}