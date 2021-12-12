package com.alwaystinkering.sandbot.ui.playlists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.model.pattern.SequencePattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.MainActivity
import com.alwaystinkering.sandbot.ui.patterns.PatternsViewModel
import kotlinx.android.synthetic.main.fragment_patterns.*
import kotlinx.android.synthetic.main.fragment_patterns.fileList
import kotlinx.android.synthetic.main.fragment_playlists.*
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

class PlaylistsFragment : Fragment() {

    private val TAG = "PatternsFragment"

    @Inject
    lateinit var viewModel: PatternsViewModel
    @Inject
    lateinit var sandbotRepository: SandbotRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ((activity as MainActivity).application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeUi()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fileListResult.refresh()
    }

    private fun subscribeUi() {
        viewModel.fileListResult.observe(viewLifecycleOwner) { result ->
            val patternList = result.sandBotFiles!!.map { sandbotRepository.createPatternFromFile(it) }.stream().filter { pattern -> pattern.fileType == FileType.SEQUENCE }
                .collect(Collectors.toList())
            patternList.sortBy { pattern -> pattern.name.lowercase(Locale.getDefault()) }
            fileList.adapter = PlaylistAdapter(patternList, sandbotRepository, viewModel)
        }

        fabAddPlaylist.setOnClickListener {
            val pattern = SequencePattern("")
            editPlaylist(pattern, it)
        }
    }

    private fun editPlaylist(pattern: AbstractPattern, view: View) {
        val direction = PlaylistsFragmentDirections.actionPlaylistsFragmentToSequenceEditFragment(pattern.name, pattern.fileType)
        view.findNavController().navigate(direction)
    }

}
