package com.alwaystinkering.sandbot.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.databinding.FragmentBotBinding
import com.alwaystinkering.sandbot.utils.InjectorUtils
import com.alwaystinkering.sandbot.viewmodels.BotViewModel
import kotlinx.android.synthetic.main.card_controls.view.*
import kotlinx.android.synthetic.main.card_led.view.*
import kotlinx.android.synthetic.main.card_status.view.*
import kotlinx.android.synthetic.main.card_storage.view.*
import java.util.*

class BotFragment : Fragment() {

    private lateinit var binding: FragmentBotBinding

    private val viewModel: BotViewModel by viewModels {
        InjectorUtils.provideBotViewModelFactory(requireContext())
    }

    var dontSetSeekbar = false
    lateinit var mainHandler: Handler

    // LED Switch handler
    private val switchHandler =
        CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            SandBotRepository.getInstance().writeLedOnOff(isChecked)
        }

    // LED Brightness slider handler
    private val brightnessHandler = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            SandBotRepository.getInstance().writeLedValue(seekBar.progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            dontSetSeekbar = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            dontSetSeekbar = false
        }
    }

    // Task to refresh status
    private val refreshStatusTask = object : Runnable {
        override fun run() {
            viewModel.botStatus.refresh()
            mainHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBotBinding.inflate(inflater, container, false)
        binding.ledCard.ledBrightnessSeekBar.max = 255

        binding.ledCard.ledSwitch.setOnCheckedChangeListener(switchHandler)
        binding.ledCard.ledBrightnessSeekBar.setOnSeekBarChangeListener(brightnessHandler)

        binding.controlsCard.commandPlay.setOnClickListener {
            SandBotRepository.getInstance().resume()
        }

        binding.controlsCard.commandPause.setOnClickListener {
            SandBotRepository.getInstance().pause()
        }

        binding.controlsCard.commandStop.setOnClickListener {
            SandBotRepository.getInstance().stop()
        }

        binding.controlsCard.commandHome.setOnClickListener {
            SandBotRepository.getInstance().home()
        }

        subscribeUi(binding)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(refreshStatusTask)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(refreshStatusTask)
    }

    private fun subscribeUi(binding: FragmentBotBinding) {
        // Listen for the file list to set the storage information
        viewModel.fileListResult.observe(viewLifecycleOwner) { result ->
            // storage card
            binding.storageCard.storageText.text = String.format(
                Locale.US,
                resources.getString(R.string.disk_usage),
                humanReadableByteCount(result.diskUsed!!, true),
                humanReadableByteCount(result.diskSize!!, true)
            )
            binding.storageCard.storageProgress.progress =
                (result.diskUsed!! * 100 / result.diskSize!!).toInt()

        }

        // Listen for the status to set the led and status information
        viewModel.botStatus.observe(viewLifecycleOwner) { result ->
            // led card
            binding.ledCard.ledSwitch.setOnCheckedChangeListener(null)
            binding.ledCard.ledSwitch.isChecked = result.ledOn == 1
            binding.ledCard.ledSwitch.setOnCheckedChangeListener(switchHandler)

            if (!dontSetSeekbar) {
                binding.ledCard.ledBrightnessSeekBar.progress = result.ledValue!!
            }

            // status card
            binding.statusCard.status_num_ops.text = result.qd.toString()
            when (result.qd) {
                0 -> {
                    binding.statusCard.status_state_text.text =
                        resources.getText(R.string.state_idle)
                    binding.statusCard.status_state_text.setBackgroundColor(resources.getColor(R.color.orange_idle))
                }
                else -> {
                    binding.statusCard.status_state_text.text =
                        resources.getText(R.string.state_running)
                    binding.statusCard.status_state_text.setBackgroundColor(resources.getColor(R.color.green_running))
                }

            }
        }
    }

    private fun humanReadableByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }
}
