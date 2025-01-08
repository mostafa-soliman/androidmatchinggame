package com.example.androidmatchinggame.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmatchinggame.databinding.ItemMeaningBinding


class MeaningsAdapter(
    private var meanings: List<String>,
    private val onEndSelected: (View) -> Unit
) : RecyclerView.Adapter<MeaningsAdapter.MeaningViewHolder>() {

    private var selectedRadioButton: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        val binding = ItemMeaningBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MeaningViewHolder(binding)
    }

    override fun getItemCount() = meanings.size

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        holder.bind(meanings[position])
    }

    fun updateMeanings(newMeanings: List<String>) {
        meanings = newMeanings
        notifyDataSetChanged()
    }

    fun resetAllRadioButtons() {
        selectedRadioButton?.isChecked = false
        selectedRadioButton = null
    }

    inner class MeaningViewHolder(private val binding: ItemMeaningBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meaning: String) {
            binding.meaningText.text = meaning
            binding.endLine.setOnClickListener {
                selectedRadioButton?.isChecked = false
                selectedRadioButton = binding.endLine
                selectedRadioButton?.isChecked = true
                onEndSelected(binding.root)
            }
        }
    }
}
