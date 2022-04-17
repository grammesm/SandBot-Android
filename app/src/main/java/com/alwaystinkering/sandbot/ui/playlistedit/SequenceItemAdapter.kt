package com.alwaystinkering.sandbot.ui.playlistedit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.databinding.CardSequenceItemBinding
import java.util.*

class SequenceItemAdapter(val dataSet: MutableList<String>) :
    RecyclerView.Adapter<SequenceItemAdapter.SequenceItemViewHolder>() {

    inner class SequenceItemViewHolder(val binding: CardSequenceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceItemViewHolder {
        val binding =
            CardSequenceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SequenceItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SequenceItemViewHolder, position: Int) {
        with(holder) {
            with(dataSet[position]) {
                binding.seqDeleteItem.setOnClickListener {
                    Log.d("SeqItemList", "Delete: ${dataSet[adapterPosition]}")
                    dataSet.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                binding.sequenceItemName.setText(this)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun moveItem(from: Int, to: Int) {
        Collections.swap(dataSet, from, to)
    }

    fun addItem(item: String) {
        if (item.isNotEmpty()) {
            dataSet.add(item)
        }
    }
}