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
import com.alwaystinkering.sandbot.databinding.FragmentPlaylistsBinding
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.model.pattern.SequencePattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.MainActivity
import com.alwaystinkering.sandbot.ui.patterns.PatternsViewModel
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

class PlaylistsFragment : Fragment() {

    companion object {
        private const val TAG = "PatternsFragment"
    }

    @Inject
    lateinit var viewModel: PatternsViewModel

    @Inject
    lateinit var sandbotRepository: SandbotRepository

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ((activity as MainActivity).application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeUi()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fileListResult.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi() {
        viewModel.fileListResult.observe(viewLifecycleOwner) { result ->
            val patternList =
                result.sandBotFiles!!.map { sandbotRepository.createPatternFromFile(it) }.stream()
                    .filter { pattern -> pattern.fileType == FileType.SEQUENCE }
                    .collect(Collectors.toList())
            patternList.sortBy { pattern -> pattern.name.lowercase(Locale.getDefault()) }
            binding.fileList.adapter = PlaylistAdapter(patternList, sandbotRepository, viewModel)
        }

        binding.fabAddPlaylist.setOnClickListener {
            val pattern = SequencePattern("")
            editPlaylist(pattern, it)
        }
    }

    private fun editPlaylist(pattern: AbstractPattern, view: View) {
        val direction = PlaylistsFragmentDirections.actionPlaylistsFragmentToSequenceEditFragment(
            pattern.name,
            pattern.fileType
        )
        view.findNavController().navigate(direction)
    }

}
