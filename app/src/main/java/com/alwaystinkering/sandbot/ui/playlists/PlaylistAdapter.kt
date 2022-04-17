package com.alwaystinkering.sandbot.ui.playlists

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.databinding.CardPatternBinding
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.patterns.PatternsViewModel

class PlaylistAdapter(
    private val data: List<AbstractPattern>,
    val sandbotRepository: SandbotRepository,
    val viewModel: PatternsViewModel
) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    companion object {
        private const val TAG = "PlaylistAdapter"
    }

    inner class PlaylistViewHolder(val binding: CardPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = CardPatternBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.patternName.text = this.name
                binding.patternRunSim.setImageResource(R.drawable.pencil)

                binding.patternRun.setOnClickListener {
                    this.let { pattern ->
                        sandbotRepository.playFile(pattern.name)
                    }
                }

                binding.patternRunSim.setOnClickListener {
                    this.let { pattern ->
                        when (pattern.fileType) {
                            FileType.SEQUENCE -> editPlaylist(pattern, it)
                            else -> Log.w(TAG, "Not Sequence")
                        }
                    }
                }

                binding.patternDelete.setOnClickListener {
                    this.let { pattern ->
                        AlertDialog.Builder(it.context)
                            .setTitle("Delete File")
                            .setMessage("Are you sure you want to delete the file " + pattern.name)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Confirm") { _, _ ->
                                sandbotRepository.deleteFile(pattern.name) { result ->
                                    if (result) {
                                        viewModel.fileListResult.refresh()
                                    }
                                }
                            }
                            .show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun editPlaylist(pattern: AbstractPattern, view: View) {
        val direction = PlaylistsFragmentDirections.actionPlaylistsFragmentToSequenceEditFragment(
            pattern.name,
            pattern.fileType
        )
        view.findNavController().navigate(direction)
    }
}