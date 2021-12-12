package com.alwaystinkering.sandbot.ui.preview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.repo.SharedPreferencesManager
import com.alwaystinkering.sandbot.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_pattern_preview.*
import javax.inject.Inject

class PatternPreviewFragment : Fragment() {

    private val TAG = "PatternPreviewFragment"
    private var running = false
    private var stop = false
    private var tableDiameter: Int = 0

    private val args: PatternPreviewFragmentArgs by navArgs()

//    @Inject
//    lateinit var viewModel: PatternPreviewViewModel
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var sandbotRepository: SandbotRepository
    lateinit var pattern: AbstractPattern

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ((activity as MainActivity).application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tableDiameter = sharedPreferencesManager.getTableDiameter()
        return inflater.inflate(R.layout.fragment_pattern_preview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeUi()
    }

    override fun onPause() {
        super.onPause()
        stop = true
    }

    private fun subscribeUi() {
        simPlay.isEnabled = false
        simPause.isEnabled = false
        simStop.isEnabled = false

        simPlay.setOnClickListener {
            startSim()
        }
        simPause.setOnClickListener {
            pauseSim()
        }
        simStop.setOnClickListener {
            stopSim()
        }

        sandbotRepository.getFile(args.filename, args.filetype).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                pattern = result
                loading.isVisible = false
                name.text = result.name
                simPlay.isEnabled = true
                simPause.isEnabled = true
                simStop.isEnabled = true
                Log.d(TAG, "Validation Result: " + pattern.validate(tableDiameter))
            } else {
                name.text = "ERROR LOADING"
            }
        }
//        viewModel.pattern.observe(viewLifecycleOwner) { result ->
//            loading.isVisible = false
//            name.text = result.name
//            simPlay.isEnabled = true
//            simPause.isEnabled = true
//            simStop.isEnabled = true
//            Log.d(TAG, "Validation Result: " + viewModel.pattern.value!!.validate(tableDiameter))
//        }
    }

    private fun startSim() {
        Log.d(TAG, "Starting Sim of pattern: " + pattern.name)
        //Log.d(TAG, "ParametricPattern Expressions: " + pattern.getExpressionString());
        stop = false
        simPlay.isEnabled = false
        simPause.isEnabled = true
        simStop.isEnabled = true

        Thread(simRunnable).start()
    }

    private fun pauseSim() {
        running = false
        simPlay.isEnabled = true
        simPause.isEnabled = false
        simStop.isEnabled = true
    }

    private fun stopSim() {
        //t = 0;
        running = false
        simulatedSandView.clear()
        pattern.reset()
        simPlay.isEnabled = true
        simPause.isEnabled = false
        simStop.isEnabled = false

    }

    private val simRunnable = Runnable {
        running = true

        var count = 0
        while (!stop && running) {

            simulatedSandView.addCoordinateAndRender(pattern.processNextEvaluation(tableDiameter), tableDiameter)
            stop = pattern.isStopped
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
            pattern.reset()
            this@PatternPreviewFragment.requireActivity().runOnUiThread {
                simPlay.isEnabled = true
                simPause.isEnabled = false
                simStop.isEnabled = true
            }
        }
    }

}
