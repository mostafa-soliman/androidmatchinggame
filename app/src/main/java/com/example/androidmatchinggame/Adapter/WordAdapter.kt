package com.example.androidmatchinggame.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmatchinggame.R
import com.example.androidmatchinggame.databinding.ItemWordBinding

class WordsAdapter(
    private var words: List<String>,
    private val onStartSelected: (View) -> Unit
) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    private var selectedRadioButton: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding)
    }

    override fun getItemCount() = words.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    fun updateWords(newWords: List<String>) {
        words = newWords
        notifyDataSetChanged()
    }

    fun resetOtherRadioButtons(currentView: View) {
        selectedRadioButton?.isChecked = false
        selectedRadioButton = currentView.findViewById(R.id.startLine)
        selectedRadioButton?.isChecked = true
    }

    fun resetAllRadioButtons() {
        selectedRadioButton?.isChecked = false
        selectedRadioButton = null
    }

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: String) {
            binding.wordText.text = word
            binding.startLine.setOnClickListener {
                onStartSelected(binding.root)
            }
        }
    }
}

