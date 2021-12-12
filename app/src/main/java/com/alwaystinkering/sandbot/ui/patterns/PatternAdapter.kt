package com.alwaystinkering.sandbot.ui.patterns

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.repo.SandbotRepository

class PatternAdapter (val data: List<AbstractPattern>, val sandbotRepository: SandbotRepository, val viewModel: PatternsViewModel) : RecyclerView.Adapter<PatternViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder  = PatternViewHolder(parent, sandbotRepository, viewModel)

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}