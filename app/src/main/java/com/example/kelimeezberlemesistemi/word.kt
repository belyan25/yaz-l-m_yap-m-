package com.example.kelimeezberlemesistemi

data class Word(
    val id: String = "",
    val ingilizce: String = "",
    val turkce: String = "",
    val ornekler: String = "",
    val eklenmeTarihi: Long = 0L,

    // YENİ EKLENENLER (6 Tekrar Algoritması İçin)
    var seviye: Int = 0,             // 0'dan başlar, 6 olunca ezberlendi sayılır
    var siradakiTestTarihi: Long = 0L // Kelimenin tekrar sorulacağı tarih (Milisaniye)
)