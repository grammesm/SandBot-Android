package com.alwaystinkering.sandbot.ui.playlistedit

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.databinding.CardSequenceFileBinding

class SequenceFileAdapter(val dataSet: List<String>) :
    RecyclerView.Adapter<SequenceFileAdapter.SequenceFileViewHolder>() {

    inner class SequenceFileViewHolder(val binding: CardSequenceFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceFileViewHolder {
        val binding =
            CardSequenceFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SequenceFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SequenceFileViewHolder, position: Int) {
        with(holder) {
            with(dataSet[position]) {
                binding.sequenceFileCard.setOnLongClickListener {
                    val data = ClipData.newPlainText("", "")
                    val shadowBuilder = View.DragShadowBuilder(it)
                    it.startDragAndDrop(data, shadowBuilder, it, 0)
                }
                binding.sequenceFileCard.tag = adapterPosition
                binding.patternName.text = this
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size
}