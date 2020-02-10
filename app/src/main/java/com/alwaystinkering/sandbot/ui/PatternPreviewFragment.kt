package com.alwaystinkering.sandbot.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.adapters.BOT_PAGE_INDEX
import com.alwaystinkering.sandbot.adapters.FileAdapter
import com.alwaystinkering.sandbot.databinding.FragmentFilesBinding
import com.alwaystinkering.sandbot.databinding.FragmentPatternPreviewBinding
import com.alwaystinkering.sandbot.utils.InjectorUtils
import com.alwaystinkering.sandbot.viewmodels.FilesViewModel
import com.alwaystinkering.sandbot.viewmodels.PatternPreviewViewModel

class PatternPreviewFragment : Fragment() {

    private val TAG = "PatternPreviewFragment"
    private var running = false
    private var stop = false

    private lateinit var binding: FragmentPatternPreviewBinding

    private val args: PatternPreviewFragmentArgs by navArgs()

    private val viewModel: PatternPreviewViewModel by viewModels {
        InjectorUtils.providePatternPreviewViewModelFactory(requireContext(), args.filename, args.filetype)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatternPreviewBinding.inflate(inflater, container, false)
        binding.simPlay.isEnabled = false
        binding.simPause.isEnabled = false
        binding.simStop.isEnabled = false

        binding.simPlay.setOnClickListener {
            startSim()
        }
        binding.simPause.setOnClickListener {
            pauseSim()
        }
        binding.simStop.setOnClickListener {
            stopSim()
        }
        subscribeUi(binding)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        stop = true
    }

    private fun subscribeUi(binding: FragmentPatternPreviewBinding) {
        viewModel.pattern.observe(viewLifecycleOwner) { result ->
            binding.name.text = result.name
            binding.simPlay.isEnabled = true
            binding.simPause.isEnabled = true
            binding.simStop.isEnabled = true
            Log.d(TAG, "Validation Result: " + viewModel.pattern.value!!.validate())
        }
    }

    private fun startSim() {
        Log.d(TAG, "Starting Sim of pattern: " + viewModel.pattern.value!!.name)
        //Log.d(TAG, "ParametricPattern Expressions: " + pattern.getExpressionString());
        stop = false
        binding.simPlay.isEnabled = false
        binding.simPause.isEnabled = true
        binding.simStop.isEnabled = true

        Thread(simRunnable).start()
    }

    private fun pauseSim() {
        running = false
        binding.simPlay.isEnabled = true
        binding.simPause.isEnabled = false
        binding.simStop.isEnabled = true
    }

    private fun stopSim() {
        //t = 0;
        running = false
        binding.simulatedSandView.clear()
        viewModel.pattern.value!!.reset()
        binding.simPlay.isEnabled = true
        binding.simPause.isEnabled = false
        binding.simStop.isEnabled = false

    }

    private val simRunnable = Runnable {
        running = true

        var count = 0
        while (!stop && running) {

            binding.simulatedSandView.addCoordinateAndRender(viewModel.pattern.value!!.processNextEvaluation())
            stop = viewModel.pattern.value!!.isStopped
            try {
                Thread.sleep(2)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            count++
        }

        Log.d(TAG, "Simulation Thread stopped")

        if (stop) {
            Log.d(TAG, "Stop condition met")
            viewModel.pattern.value!!.reset()
            this@PatternPreviewFragment.activity!!.runOnUiThread {
                binding.simPlay.isEnabled = true
                binding.simPause.isEnabled = false
                binding.simStop.isEnabled = true
            }
        }
    }

}
