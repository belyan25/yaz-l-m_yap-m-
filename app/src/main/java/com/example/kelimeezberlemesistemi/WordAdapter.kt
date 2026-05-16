package com.example.kelimeezberlemesistemi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(private val wordList: List<Word>) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEnglish: TextView = view.findViewById(R.id.tvEnglishWord) // Kendi layout id'nizle eşleşmeli
        val tvTurkish: TextView = view.findViewById(R.id.tvTurkishWord) // Kendi layout id'nizle eşleşmeli
        val tvStatus: TextView = view.findViewById(R.id.tvWordStatus)   // Kendi layout id'nizle eşleşmeli
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false) // item_word tasarımıyla eşleşir
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = wordList[position]

        // --- BURAYI YENİ WORD MODELİNE GÖRE DÜZELTTİK ---
        holder.tvEnglish.text = currentWord.ingilizce
        holder.tvTurkish.text = currentWord.turkce
        holder.tvStatus.text = currentWord.ornekler
    }

    override fun getItemCount(): Int {
        return wordList.size
    }
}