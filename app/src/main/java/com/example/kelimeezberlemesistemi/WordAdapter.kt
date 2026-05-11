package com.example.kelimeezberlemesistemi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(private val wordList: List<Word>) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // BURASI DEĞİŞTİ: XML'deki yeni id'ler ile eşitledik
        val tvEng: TextView = view.findViewById(R.id.tvEnglishWord)
        val tvTr: TextView = view.findViewById(R.id.tvTurkishMeaning)

        // Eğer ilerleme (progress) bilgisini yeni kartta kullanmayacaksan
        // bu satırı silebilirsin ya da XML'e bir TextView daha ekleyebilirsin.
        // Şimdilik hata vermemesi için aşağıya bağlıyoruz.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = wordList[position]
        holder.tvEng.text = word.eng
        holder.tvTr.text = word.tr

        // Eğer kart tasarımında 'tvProgress' yoksa bu satır kırmızı yanabilir.
        // Kart tasarımında şimdilik buna yer vermedik, istersen yorum satırına al:
        // holder.tvProg.text = word.progress
    }

    override fun getItemCount() = wordList.size
}