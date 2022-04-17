package com.alwaystinkering.sandbot.ui.playlistedit

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.databinding.FragmentSequenceEditBinding
import com.alwaystinkering.sandbot.databinding.TagPlaylistBinding
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.model.pattern.SequencePattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.MainActivity
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.collections.ArrayList

class SequenceEditFragment : Fragment() {

    private val TAG = "SequenceEditFragment"

    private val args: SequenceEditFragmentArgs by navArgs()

    @Inject
    lateinit var viewModel: SequenceEditViewModel
    @Inject
    lateinit var sandbotRepository: SandbotRepository
    lateinit var pattern: AbstractPattern

    private var _binding: FragmentSequenceEditBinding? = null
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
        _binding = FragmentSequenceEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.saveButton.setOnClickListener {
            val newName = binding.sequenceName.text.toString().trim()
            if (newName.isNotEmpty()) {
                pattern.name = newName + "." + pattern.fileType?.extension
                (pattern as SequencePattern).sequenceContents = (binding.sequenceList.adapter as SequenceItemAdapter).dataSet
                sandbotRepository.saveFile(pattern as SequencePattern)
            } else {
                AlertDialog.Builder(it.context)
                    .setTitle("Playlist Error")
                    .setMessage("Playlist name can not be blank")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        binding.sequenceRepeatButton.text.text = requireContext().getString(R.string.repeat_button_text)
        binding.sequenceRepeatButton.root.tag = requireContext().getString(R.string.repeat_entry)
        binding.sequenceRepeatButton.root.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        binding.sequenceNoRepeatButton.text.text = requireContext().getString(R.string.repeat_end_button_text)
        binding.sequenceNoRepeatButton.root.tag = requireContext().getString(R.string.repeat_end_entry)
        binding.sequenceNoRepeatButton.root.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        binding.sequenceShuffleButton.text.text = requireContext().getString(R.string.shuffle_button_text)
        binding.sequenceShuffleButton.root.tag = requireContext().getString(R.string.shuffle_entry)
        binding.sequenceShuffleButton.root.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        binding.sequenceNoShuffleButton.text.text = requireContext().getString(R.string.shuffle_end_button_text)
        binding.sequenceNoShuffleButton.root.tag = requireContext().getString(R.string.shuffle_end_entry)
        binding.sequenceNoShuffleButton.root.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }

        binding.sequenceList.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val viewSource = event.localState as View
                    val viewId = v.id

                    var target: RecyclerView? = null
                    when (viewId) {
                        R.id.sequenceList -> {
                            Log.d("Drop","SequenceList view")
                            target = v as RecyclerView
                        }
                        R.id.fileList -> {
                            Log.d("Drop","FileList view")
                            target = v as RecyclerView
                        }
                        else -> {
                            target = v.parent as RecyclerView
                        }
                    }
                    val adapter = target?.adapter as SequenceItemAdapter
                    var item = ""
                    when (viewSource) {
                        is CardView -> {
                            Log.d("Drop", "CardView source")
                            val sourceAdapter = (viewSource.parent as RecyclerView).adapter as SequenceFileAdapter
                            item = sourceAdapter.dataSet[viewSource.tag as Int]
                        }
                        is Button -> {
                            Log.d("Drop", "Button source")
                            item = viewSource.tag as String
                        }
                        is PlaylistTag -> {
                            Log.d("Drop", "PlaylistTag source")
                            item = viewSource.tag as String
                        }
                    }
                    adapter.addItem(item)
                    adapter.notifyItemInserted(adapter.dataSet.size - 1)
                }
                DragEvent.ACTION_DRAG_ENTERED ->
                    binding.sequenceList.setBackgroundResource(R.drawable.list_highlighted_bg)
                DragEvent.ACTION_DRAG_ENDED ->
                    binding.sequenceList.setBackgroundResource(R.drawable.list_normal_bg)
                DragEvent.ACTION_DRAG_EXITED ->
                    binding.sequenceList.setBackgroundResource(R.drawable.list_normal_bg)
            }
            true
        }
        subscribeUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fileList.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi() {

        if (args.filename.isNotEmpty()) {
            sandbotRepository.getFile(args.filename, args.filetype)
                .observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        pattern = result
                        if (result is SequencePattern) {
                            val stringList: ArrayList<String> = ArrayList(result.sequenceContents);
                            Log.d(TAG, result.sequenceContents.toString())
                            binding.sequenceList.adapter = SequenceItemAdapter(stringList)
                            reorderTouchHelper.attachToRecyclerView(binding.sequenceList)
                            binding.sequenceName.setText(pattern.name?.replace(".seq", ""))
                        }
                    }
                }
        } else {
            val stringList: ArrayList<String> = ArrayList()
            binding.sequenceList.adapter = SequenceItemAdapter(stringList)
            reorderTouchHelper.attachToRecyclerView(binding.sequenceList)
            binding.sequenceName.hint = "Enter Name"
            pattern = SequencePattern("")
            binding.sequenceName.setText(pattern.name)
        }

        viewModel.fileList.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "File List Result")
            Log.d(
                TAG,
                result.stream().map(AbstractPattern::getName).collect(Collectors.toList())
                    .toString()
            )
            val fileList = result.stream()
                .filter { pattern -> pattern.fileType != FileType.SEQUENCE }
                .map(AbstractPattern::getName)
                .collect(Collectors.toList())
            fileList.sortBy { item -> item.toLowerCase(Locale.getDefault()) }
            binding.filesList.adapter = SequenceFileAdapter(fileList)
        }
    }

    private val reorderTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val adapter = recyclerView.adapter as SequenceItemAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }


}
