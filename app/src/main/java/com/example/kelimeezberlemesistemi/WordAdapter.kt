package com.example.kelimeezberlemesistemi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(private val wordList: List<Word>) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEng: TextView = view.findViewById(R.id.tvEngWord)
        val tvTr: TextView = view.findViewById(R.id.tvTrWord)
        val tvProg: TextView = view.findViewById(R.id.tvProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = wordList[position]
        holder.tvEng.text = word.eng
        holder.tvTr.text = word.tr
        holder.tvProg.text = word.progress
    }

    override fun getItemCount() = wordList.size
}