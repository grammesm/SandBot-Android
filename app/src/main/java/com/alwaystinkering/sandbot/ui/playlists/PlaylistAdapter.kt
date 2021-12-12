package com.alwaystinkering.sandbot.ui.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.patterns.PatternsViewModel

class PlaylistAdapter(val data: List<AbstractPattern>, val sandbotRepository: SandbotRepository, val viewModel: PatternsViewModel) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(parent, sandbotRepository, viewModel)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}