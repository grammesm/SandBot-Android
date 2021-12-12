package com.alwaystinkering.sandbot.ui.playlistedit

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.util.inflater
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_sequence_item.*
import java.util.*

class SequenceItemAdapter(val dataSet: MutableList<String>) :
    RecyclerView.Adapter<SequenceItemAdapter.SequenceItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceItemViewHolder =
        SequenceItemViewHolder(parent)

    override fun onBindViewHolder(holder: SequenceItemViewHolder, position: Int) {
        holder.bind(dataSet[position])
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

    inner class SequenceItemViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflater(R.layout.card_sequence_item)), LayoutContainer {

        private val TAG = "SequenceItemViewHolder"

        override val containerView: View?
            get() = itemView

        fun bind(item: String) {
            seqDeleteItem.setOnClickListener {
                Log.d("SeqItemList", "Delete: ${dataSet[adapterPosition]}")
                dataSet.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            sequenceItemName.setText(item)
        }
    }
}