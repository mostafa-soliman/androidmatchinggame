package com.example.androidmatchinggame.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmatchinggame.R
import com.example.androidmatchinggame.databinding.ItemWordBinding

class WordsAdapter(
    private var words: List<String>,
    private val onWordTouch: (View, String, MotionEvent) -> Boolean
    // private val onWordSelected: (View, String) -> Unit  // تم تعديل الواجهة لتمرير النص مع العرض
) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    private var selectedView: View? = null  // تتبع العنصر المحدد

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding)
    }

    fun getWordAtPosition(position: Int): String {
        return words[position]
    }

    override fun getItemCount() = words.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    fun updateWords(newWords: List<String>) {
        words = newWords
        notifyDataSetChanged()
    }


    fun resetSelection() {
        selectedView?.isSelected = false
        selectedView = null
    }

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(word: String) {
            binding.wordText.text = word
            binding.wordText.setOnTouchListener { view, event ->
                // استدعاء الدالة الممررة لمعالجة onTouchEvent
                onWordTouch(view, word, event)
            }

        }
    }
}
