package com.alwaystinkering.sandbot.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.SandBotViewPagerFragmentDirections
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.databinding.CardFileBinding
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.google.android.material.snackbar.Snackbar


/**
 * Adapter for the [RecyclerView] in [FilesFragment].
 */
class FileAdapter : ListAdapter<AbstractPattern, RecyclerView.ViewHolder>(FileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlantViewHolder(
            CardFileBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant = getItem(position)
        (holder as PlantViewHolder).bind(plant)
    }

    class PlantViewHolder(
        private val binding: CardFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // TODO this not working?
            binding.patternRunSim.visibility = if (binding.pattern?.fileType != FileType.SEQUENCE) VISIBLE else GONE;
            binding.patternRun.setOnClickListener {
                binding.pattern?.let { pattern ->
                    SandBotRepository.getInstance().playFile(pattern.name)
                }
            }
            binding.patternRunSim.setOnClickListener {
                binding.pattern?.let { pattern ->
                    when (pattern.fileType) {
                        FileType.SEQUENCE -> Snackbar.make(it, "Sequence Preview Not Implemented", Snackbar.LENGTH_LONG).show()
                        else -> navigateToPreview(pattern, it)
                    }
                }
            }
        }

        private fun navigateToPreview(
            pattern: AbstractPattern,
            view: View
        ) {
            val direction =
                SandBotViewPagerFragmentDirections.actionHomeViewPagerFragmentToPatternPreviewFragment(
                    pattern.name,
                    pattern.fileType
                )
            view.findNavController().navigate(direction)
        }

        fun bind(item: AbstractPattern) {
            binding.apply {
                pattern = item
                executePendingBindings()
            }
        }
    }
}

private class FileDiffCallback : DiffUtil.ItemCallback<AbstractPattern>() {

    override fun areItemsTheSame(oldItem: AbstractPattern, newItem: AbstractPattern): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: AbstractPattern, newItem: AbstractPattern): Boolean {
        return oldItem == newItem
    }
}