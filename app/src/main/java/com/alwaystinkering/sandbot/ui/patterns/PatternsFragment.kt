package com.alwaystinkering.sandbot.ui.patterns

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_patterns.*
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

class PatternsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_patterns, container, false)
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
            val patternList = result.sandBotFiles!!.map { sandbotRepository.createPatternFromFile(it) }.stream().filter { pattern -> pattern.fileType != FileType.SEQUENCE }
                .collect(Collectors.toList())
            patternList.sortBy { pattern -> pattern.name.lowercase(Locale.getDefault()) }
            fileList.adapter = PatternAdapter(patternList, sandbotRepository, viewModel)
        }
    }
}
