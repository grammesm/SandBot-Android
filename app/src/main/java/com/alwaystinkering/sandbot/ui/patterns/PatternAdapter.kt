package com.alwaystinkering.sandbot.ui.patterns

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.databinding.CardPatternBinding
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.repo.SandbotRepository

class PatternAdapter(
    val data: List<AbstractPattern>,
    val sandbotRepository: SandbotRepository,
    val viewModel: PatternsViewModel
) : RecyclerView.Adapter<PatternAdapter.PatternViewHolder>() {

    companion object {
        private const val TAG = "PatternAdapter"
    }

    inner class PatternViewHolder(val binding: CardPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder {
        val binding: CardPatternBinding =
            CardPatternBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatternViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.patternName.text = this.name

                binding.patternRun.setOnClickListener {
                    this.let { pattern ->
                        sandbotRepository.playFile(pattern.name)
                    }
                }

                binding.patternRunSim.setOnClickListener {
                    this.let { pattern ->
                        when (pattern.fileType) {
                            FileType.SEQUENCE -> Log.w(TAG, "Sequence")
                            else -> previewPattern(pattern, it)
                        }
                    }
                }

                binding.patternDelete.setOnClickListener {
                    this.let { pattern ->
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
        }
    }

    override fun getItemCount(): Int = data.size

    private fun previewPattern(pattern: AbstractPattern, view: View) {
        val direction = PatternsFragmentDirections.actionPatternsFragmentToPatternPreviewFragment(
            pattern.name,
            pattern.fileType
        )
        view.findNavController().navigate(direction)
    }

}