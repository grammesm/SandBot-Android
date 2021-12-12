package com.alwaystinkering.sandbot.ui.playlists

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.patterns.PatternsViewModel
import com.alwaystinkering.sandbot.util.inflater
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_pattern.*

class PlaylistViewHolder(parent: ViewGroup, private val sandbotRepository: SandbotRepository, private val viewModel: PatternsViewModel) :
    RecyclerView.ViewHolder(parent.inflater(R.layout.card_pattern)), LayoutContainer {

    private val TAG = "PlaylistViewHolder"

    override val containerView: View?
        get() = itemView

    fun bind(item: AbstractPattern) {

        patternName.text = item.name
        patternRunSim.setImageResource(R.drawable.pencil)

        patternRun.setOnClickListener {
            item.let { pattern ->
                sandbotRepository.playFile(pattern.name)
            }
        }

        patternRunSim.setOnClickListener {
            item.let { pattern ->
                when (pattern.fileType) {
                    FileType.SEQUENCE -> editPlaylist(pattern, it)
                    else -> Log.w(TAG, "Not Sequence")
                }
            }
        }

        patternDelete.setOnClickListener {
            item.let { pattern ->
                AlertDialog.Builder(it.context)
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete the file " + pattern.name)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm") { dialog, which ->
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

    private fun editPlaylist(pattern: AbstractPattern, view: View) {
        val direction = PlaylistsFragmentDirections.actionPlaylistsFragmentToSequenceEditFragment(pattern.name, pattern.fileType)
        view.findNavController().navigate(direction)
    }
}