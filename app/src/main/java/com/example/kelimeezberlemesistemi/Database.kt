package com.example.kelimeezberlemesistemi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Calendar

class Database(context: Context) : SQLiteOpenHelper(context, "KelimeEzber.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createWordsTable = ("CREATE TABLE Words (" +
                "WordID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EngWordName TEXT, " +
                "TurWordName TEXT, " +
                "Picture TEXT, " +
                "Level INTEGER DEFAULT 0, " +
                "NextDate INTEGER DEFAULT 0)")

        val createSamplesTable = ("CREATE TABLE WordSamples (" +
                "WordSamplesID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WordID INTEGER, " +
                "Samples TEXT, " +
                "FOREIGN KEY(WordID) REFERENCES Words(WordID))")

        db?.execSQL(createWordsTable)
        db?.execSQL(createSamplesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS WordSamples")
        db?.execSQL("DROP TABLE IF EXISTS Words")
        onCreate(db)
    }

    // Kelime Ekleme
    fun addWordWithSample(eng: String, tur: String, pic: String, sample: String): Long {
        val db = this.writableDatabase
        val wordValues = ContentValues().apply {
            put("EngWordName", eng)
            put("TurWordName", tur)
            put("Picture", pic)
            put("Level", 0)
            put("NextDate", System.currentTimeMillis())
        }
        val wordId = db.insert("Words", null, wordValues)
        if (wordId != -1L && sample.isNotEmpty()) {
            val sampleValues = ContentValues().apply {
                put("WordID", wordId)
                put("Samples", sample)
            }
            db.insert("WordSamples", null, sampleValues)
        }
        return wordId
    }

    // --- ANALİZ RAPORU İÇİN GEREKLİ FONKSİYON ---
    fun getReportData(): Map<String, Int> {
        val db = this.readableDatabase
        val stats = mutableMapOf<String, Int>()

        // 1. Toplam Kelime
        val cursorTotal = db.rawQuery("SELECT COUNT(*) FROM Words", null)
        if (cursorTotal.moveToFirst()) stats["total"] = cursorTotal.getInt(0)
        cursorTotal.close()

        // 2. Seviye 0 (Yeni)
        val cursorLvl0 = db.rawQuery("SELECT COUNT(*) FROM Words WHERE Level = 0", null)
        if (cursorLvl0.moveToFirst()) stats["lvl0"] = cursorLvl0.getInt(0)
        cursorLvl0.close()

        // 3. Seviye 1-3 (Öğreniliyor)
        val cursorLvl13 = db.rawQuery("SELECT COUNT(*) FROM Words WHERE Level BETWEEN 1 AND 3", null)
        if (cursorLvl13.moveToFirst()) stats["lvl13"] = cursorLvl13.getInt(0)
        cursorLvl13.close()

        // 4. Seviye 4-6 (Öğrenildi)
        val cursorLvl46 = db.rawQuery("SELECT COUNT(*) FROM Words WHERE Level BETWEEN 4 AND 6", null)
        if (cursorLvl46.moveToFirst()) stats["lvl46"] = cursorLvl46.getInt(0)
        cursorLvl46.close()

        return stats
    }
}