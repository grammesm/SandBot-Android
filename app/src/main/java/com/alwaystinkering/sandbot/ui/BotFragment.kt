package com.alwaystinkering.sandbot.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.SandBotViewPagerFragmentDirections
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.databinding.FragmentBotBinding
import com.alwaystinkering.sandbot.utils.InjectorUtils
import com.alwaystinkering.sandbot.viewmodels.BotViewModel
import java.util.*

class BotFragment : Fragment() {

    private val TAG = "BotFragment"
    private lateinit var binding: FragmentBotBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var runTask = false

    private val prefListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                resources.getString(R.string.pref_server_key) -> {
                    var ip = sharedPreferences.getString(
                        resources.getString(R.string.pref_server_key),
                        resources.getString(R.string.pref_server_default)
                    )!!.trim()

                    if (ip != resources.getString(R.string.pref_server_default)) {
                        SandBotRepository.getInstance().createNewConnection(ip)
                        if (!runTask) {
                            runTask = true
                            mainHandler.post(refreshStatusTask)
                        }
                    }
                }
                resources.getString(R.string.pref_diameter_key) -> Log.d(TAG, "diameter changed");
            }
        }

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
        Log.d(TAG, "onCreate")
        mainHandler = Handler(Looper.getMainLooper())
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)


        val serverIp = sharedPreferences.getString(
            resources.getString(R.string.pref_server_key),
            resources.getString(R.string.pref_server_default)
        )!!.trim()

        if (serverIp == resources.getString(R.string.pref_server_default)) {
            runTask = false
            AlertDialog.Builder(requireContext())
                .setTitle("Enter Robot IP Address")
                .setMessage("It appears you do not have an IP address set up for the sand bot, press OK to be taken to settings to enter the IP address of the bot.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK") { dialog, which ->
                    navigateToSettings(this@BotFragment.requireView())
                }.show()
        } else {
            SandBotRepository.getInstance().createNewConnection(serverIp)
            runTask = true
        }
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
        if (runTask) {
            mainHandler.post(refreshStatusTask)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(refreshStatusTask)
    }

    private fun subscribeUi(binding: FragmentBotBinding) {
        // Listen for the file list to set the storage information
        viewModel.fileListResult.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "File List Result")
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

        val connectedObserver = Observer<Boolean> { connected ->
            Log.d(TAG, "Connected changed: " + connected)
            if (!connected) {
                binding.statusCard.statusStateText.text =
                    resources.getText(R.string.state_disconnected)
                binding.statusCard.statusStateText.setBackgroundColor(resources.getColor(R.color.red_error))
                binding.ledCard.ledSwitch.isEnabled = false
                binding.ledCard.ledBrightnessSeekBar.isEnabled = false
                binding.statusCard.statusNumOps.text = "0"
                // storage card
                binding.storageCard.storageText.text = String.format(
                    Locale.US,
                    resources.getString(R.string.disk_usage),
                    humanReadableByteCount(0, true),
                    humanReadableByteCount(0, true)
                )
            } else {
                viewModel.fileListResult.refresh()
            }
        }

        SandBotRepository.getInstance().connected.observe(viewLifecycleOwner, connectedObserver)

        // Listen for the status to set the led and status information
        viewModel.botStatus.observe(viewLifecycleOwner) { result ->

            // led card
            binding.ledCard.ledSwitch.isEnabled = true
            binding.ledCard.ledBrightnessSeekBar.isEnabled = true
            binding.ledCard.ledSwitch.setOnCheckedChangeListener(null)
            binding.ledCard.ledSwitch.isChecked = result.ledOn == 1
            binding.ledCard.ledSwitch.setOnCheckedChangeListener(switchHandler)

            if (!dontSetSeekbar) {
                binding.ledCard.ledBrightnessSeekBar.progress = result.ledValue!!
            }

            // status card
            binding.statusCard.statusNumOps.text = result.qd.toString()
            when (result.qd) {
                0 -> {
                    binding.statusCard.statusStateText.text =
                        resources.getText(R.string.state_idle)
                    binding.statusCard.statusStateText.setBackgroundColor(resources.getColor(R.color.orange_idle))
                }
                else -> {
                    binding.statusCard.statusStateText.text =
                        resources.getText(R.string.state_running)
                    binding.statusCard.statusStateText.setBackgroundColor(resources.getColor(R.color.green_running))
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

    private fun navigateToSettings(
        view: View
    ) {
        val direction =
            SandBotViewPagerFragmentDirections.actionHomeViewPagerFragmentToSettingsFragment()
        view.findNavController().navigate(direction)
    }
}
