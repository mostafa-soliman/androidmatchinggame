package com.example.androidmatchinggame.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmatchinggame.databinding.ItemMeaningBinding

class MeaningsAdapter(
    private var meanings: List<String>,
    private val onMeaningTouch: (View, String, MotionEvent) -> Boolean
    // private val onMeaningSelected: (View, String) -> Unit
) : RecyclerView.Adapter<MeaningsAdapter.MeaningViewHolder>() {

    private var selectedView: View? = null

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

    fun getMeaningAtPosition(position: Int): String {
        return meanings[position]
    }

    fun resetSelection() {
        selectedView?.isSelected = false
        selectedView = null
    }

    inner class MeaningViewHolder(private val binding: ItemMeaningBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(meaning: String) {
            binding.meaningText.text = meaning
            binding.meaningText.setOnTouchListener { view, event ->
                // استدعاء الدالة الممررة لمعالجة onTouchEvent
                onMeaningTouch(view, meaning, event)
            }

        }
    }
}
