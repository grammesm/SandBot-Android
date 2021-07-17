package com.alwaystinkering.sandbot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.adapters.BOT_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.FileAdapter
import com.alwaystinkering.sandbot.databinding.FragmentFilesBinding
import com.alwaystinkering.sandbot.utils.InjectorUtils
import com.alwaystinkering.sandbot.viewmodels.FilesViewModel

class FilesFragment : Fragment() {

    private lateinit var binding: FragmentFilesBinding

    private val viewModel: FilesViewModel by viewModels {
        InjectorUtils.provideFilesViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        val adapter = FileAdapter()
        binding.fileList.adapter = adapter
        subscribeUi(adapter, binding)
        return binding.root
    }

    private fun subscribeUi(adapter: FileAdapter, binding: FragmentFilesBinding) {
        //binding.hasPlantings = !result.isNullOrEmpty()
        viewModel.fileList.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
        }
    }

    // TODO: convert to data binding if applicable
    private fun navigateToBotPage() {
        requireActivity().findViewById<ViewPager2>(R.id.view_pager).currentItem =
            BOT_PAGE_INDEX
    }
}
