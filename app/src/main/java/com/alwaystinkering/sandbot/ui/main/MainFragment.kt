package com.alwaystinkering.sandbot.ui.main

import android.content.Context
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
import androidx.navigation.fragment.findNavController
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.api.BotStatus
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.repo.SharedPreferencesManager
import com.alwaystinkering.sandbot.ui.MainActivity
import kotlinx.android.synthetic.main.card_controls.*
import kotlinx.android.synthetic.main.card_led.*
import kotlinx.android.synthetic.main.card_status.*
import kotlinx.android.synthetic.main.card_storage.*
import java.util.*
import javax.inject.Inject

class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var sandbotRepository: SandbotRepository

    lateinit var mainHandler: Handler
    var dontSetSeekbar = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ((activity as MainActivity).application as App).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
        if (sharedPreferencesManager.getServeIpAddress() == requireContext().getString(R.string.pref_server_default)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Enter Robot IP Address")
                .setMessage("It appears you do not have an IP address set up for the sand bot, press OK to be taken to settings to enter the IP address of the bot.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK") { dialog, which ->
                    navigateToSettings()
                }.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setControlsEnabled(false)
        bindView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fileListResult.refresh()
        mainHandler.post(refreshStatusTask)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(refreshStatusTask)
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_main, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
//    }
//
    private fun navigateToSettings() {
        val direction = MainFragmentDirections.actionMainFragmentToSettingsFragment()
        findNavController().navigate(direction)
    }

    // Task to refresh status
    private val refreshStatusTask = object : Runnable {
        override fun run() {
            Log.d(TAG, "Refreshing status");
            viewModel.botStatus.refresh()
            mainHandler.postDelayed(this, 5000)
        }
    }

    // LED Switch handler
    private val switchHandler =
        CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            ledBrightnessSeekBar.isEnabled = isChecked
            sandbotRepository.writeLedOnOff(isChecked)
        }

    // LED Brightness slider handler
    private val brightnessHandler = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            sandbotRepository.writeLedValue(seekBar.progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            dontSetSeekbar = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            dontSetSeekbar = false
        }
    }


    private fun bindView() {
        ledSwitch.setOnCheckedChangeListener(switchHandler)
        ledBrightnessSeekBar.setOnSeekBarChangeListener(brightnessHandler)

        commandPlay.setOnClickListener {
            sandbotRepository.resume()
        }

        commandPause.setOnClickListener {
            sandbotRepository.pause()
        }

        commandStop.setOnClickListener {
            sandbotRepository.stop()
        }

        commandHome.setOnClickListener {
            sandbotRepository.home()
        }

        viewModel.fileListResult.observe(viewLifecycleOwner) { result ->
            if (result == null) {
                storageText.text = "--/--"
            } else {
                // Update the storage information
                storageText.text = String.format(
                    Locale.US,
                    resources.getString(R.string.disk_usage),
                    humanReadableByteCount(result.diskUsed!!, true),
                    humanReadableByteCount(result.diskSize!!, true)
                )
                storageProgress.progress =
                    (result.diskUsed!! * 100 / result.diskSize!!).toInt()
            }
        }

        viewModel.botStatus.observe(viewLifecycleOwner) { status ->
            if (status != null) {
                setControlsEnabled(true)
                updateStatus(status)
            } else {
                setControlsEnabled(false)
            }
        }
    }

    private fun updateStatus(botStatus: BotStatus) {

        ledSwitch.setOnCheckedChangeListener(null)
        ledSwitch.isChecked = botStatus.ledOn == 1
        ledBrightnessSeekBar.isEnabled = ledSwitch.isChecked
        ledSwitch.setOnCheckedChangeListener(switchHandler)

        if (!dontSetSeekbar) {
            ledBrightnessSeekBar.progress = botStatus.ledValue!!
        }

        status_num_ops.text = botStatus.qd.toString()
        when (botStatus.qd) {
            0 -> {
                status_state_text.text =
                    resources.getText(R.string.state_idle)
                status_state_text.setBackgroundColor(requireContext().getColor(R.color.orange_idle))
            }
            else -> {
                status_state_text.text =
                    resources.getText(R.string.state_running)
                status_state_text.setBackgroundColor(requireContext().getColor(R.color.green_running))
            }

        }
    }

    private fun setControlsEnabled(enabled: Boolean) {
        ledSwitch.isEnabled = enabled
        ledBrightnessSeekBar.isEnabled = enabled && ledSwitch.isChecked
        commandPlay.isEnabled = enabled
        commandHome.isEnabled = enabled
        commandPause.isEnabled = enabled
        commandStop.isEnabled = enabled
        if (enabled) {

        } else {
            status_state_text.text =
                resources.getText(R.string.state_disconnected)
            status_state_text.setBackgroundColor(requireContext().getColor(R.color.red_error))
            status_num_ops.text = "--"
            storageText.text = "--/--"
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