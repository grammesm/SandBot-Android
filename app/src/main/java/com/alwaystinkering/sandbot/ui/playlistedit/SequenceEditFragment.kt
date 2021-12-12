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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.App
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.model.pattern.SequencePattern
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_sequence_edit.*
import java.util.stream.Collectors
import javax.inject.Inject

class SequenceEditFragment : Fragment() {

    private val TAG = "SequenceEditFragment"

    private val args: SequenceEditFragmentArgs by navArgs()

    @Inject
    lateinit var viewModel: SequenceEditViewModel
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
        return inflater.inflate(R.layout.fragment_sequence_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        saveButton.setOnClickListener {
            val newName = sequenceName.text.toString().trim()
            if (newName.isNotEmpty()) {
                pattern.name = newName + "." + pattern.fileType?.extension
                (pattern as SequencePattern).sequenceContents = (sequenceList.adapter as SequenceItemAdapter).dataSet
                sandbotRepository.saveFile(pattern as SequencePattern)
            } else {
                AlertDialog.Builder(it.context)
                    .setTitle("Playlist Error")
                    .setMessage("Playlist name can not be blank")
                    .show()
            }
        }

        sequenceRepeatButton.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        sequenceNoRepeatButton.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        sequenceShuffleButton.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        sequenceNoShuffleButton.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }

        sequenceList.setOnDragListener { v, event ->
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
                    }
                    adapter.addItem(item)
                    adapter.notifyItemInserted(adapter.dataSet.size - 1)
                }
            }
            true
        }
        subscribeUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fileList.refresh()
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
                            sequenceList.adapter = SequenceItemAdapter(stringList)
                            reorderTouchHelper.attachToRecyclerView(sequenceList)
                            sequenceName.setText(pattern.name?.replace(".seq", ""))
                        }
                    }
                }
        } else {
            val stringList: ArrayList<String> = ArrayList()
            sequenceList.adapter = SequenceItemAdapter(stringList)
            reorderTouchHelper.attachToRecyclerView(sequenceList)
            sequenceName.hint = "Enter Name"
            pattern = SequencePattern("")
            sequenceName.setText(pattern.name)
        }

        viewModel.fileList.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "File List Result")
            Log.d(
                TAG,
                result.stream().map(AbstractPattern::getName).collect(Collectors.toList())
                    .toString()
            )
            filesList.adapter = SequenceFileAdapter(
                result.stream()
                    .filter { pattern -> pattern.fileType != FileType.SEQUENCE }
                    .map(AbstractPattern::getName)
                    .collect(Collectors.toList())
            )
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
