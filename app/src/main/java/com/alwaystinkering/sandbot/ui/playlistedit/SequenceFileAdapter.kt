package com.alwaystinkering.sandbot.ui.playlistedit

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SequenceFileAdapter(val dataSet: List<String>) :
    RecyclerView.Adapter<SequenceFileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceFileViewHolder =
        SequenceFileViewHolder(parent)

    override fun onBindViewHolder(holder: SequenceFileViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size
}