# 🐰 CatchyWords - Akıllı Kelime Ezberleme Sistemi

CatchyWords, kullanıcıların İngilizce kelimeleri kalıcı olarak öğrenmelerini sağlayan, aralıklı tekrar (spaced repetition) algoritmasına ve yapay zeka destekli görsel-işitsel araçlara sahip modern bir Android uygulamasıdır.

## 🚀 Projenin Öne Çıkan Özellikleri

* **Bulut Tabanlı Veri Yönetimi:** Tüm kelimeler ve kullanıcı ilerlemeleri **Firebase Realtime Database** üzerinde tutulur. Kullanıcılar farklı cihazlardan hesaplarına erişebilirler.
* **6-Sefer Kuralı (Spaced Repetition):** Kelimeler rastgele değil, kullanıcının başarı durumuna göre hesaplanan dinamik tarihlerde (1, 7, 30, 90, 180 ve 365 gün) tekrar test edilir.
* **Sesli Kelime Ekleme:** Google Speech-to-Text API sayesinde klavye kullanmadan, mikrofona konuşarak kelime eklenebilir.
* **AI Word Chain (Yapay Zeka Hikaye Atölyesi):** Kullanıcının girdiği kelimeler **Gemini 2.5 Flash LLM** modeli ile anlamlı bir hikayeye dönüştürülür. Eş zamanlı olarak **Pollinations.ai** üzerinden bu kelimelere ait fantastik görseller sentezlenir ve ekranda video (slayt) geçiş efektleriyle sunulur.
* **Otomatik PDF Karne:** Öğrencinin gelişim süreci, başarı oranı ve ezberlediği kelime havuzu tek tuşla profesyonel bir PDF raporuna dönüştürülür.
* **Modern UI/UX:** Material Design kurallarına uygun, kullanıcı dostu ve akıcı arayüz.

## 🛠️ Kullanılan Teknolojiler
* **Dil:** Kotlin
* **Mimari:** Android SDK (Minimum API 24, Target API 36)
* **Backend:** Firebase (Authentication, Realtime Database)
* **Yapay Zeka:** Google Generative AI (Gemini), Pollinations Image Synthesis
* **Asenkron İşlemler:** Kotlin Coroutines & Lifecycle Scopes

## 👥 Geliştirici Ekip
* Yağız Belyan Çelik 242804005 - *Yazılım & Mimari*
* Kamile Ülkem Arslan  242802041 - *UI/UX Tasarım *
* Tuncay Per 242802049 - *Backend*

## 📋Beyan Tablosu

| :--- | :--- |
| Kullanıcı Kayıt Modülü hazırladığınız yazılımda var mı? | **Evet** |
| Kelime ekleme modülü yazılımda var mı? | **Evet** |
| Kelime sorgulama modülü (test modülü) hazırladığınız yazılımda var mı? | **Evet** |
| Kelime sıklığı değiştirme Modülü hazırladığınız yazılımda var mı? | **Evet** |
| Analiz Rapor Modülü hazırladığınız yazılımda var mı? | **Evet** |
| Bulmaca Modülü | **Evet** |
| LLM Modülü | **Evet** |
