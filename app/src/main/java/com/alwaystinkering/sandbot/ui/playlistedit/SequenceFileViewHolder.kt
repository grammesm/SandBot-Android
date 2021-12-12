package com.alwaystinkering.sandbot.ui.playlistedit

import android.content.ClipData
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alwaystinkering.sandbot.R
import com.alwaystinkering.sandbot.util.inflater
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_sequence_file.*

class SequenceFileViewHolder(parent: ViewGroup):
    RecyclerView.ViewHolder(parent.inflater(R.layout.card_sequence_file)), LayoutContainer {

    private val TAG = "SequenceFileViewHolder"

    override val containerView: View?
        get() = itemView

    fun bind(item: String) {
        sequenceFileCard.setOnLongClickListener {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, shadowBuilder, it, 0)
        }
        sequenceFileCard.tag = adapterPosition
        patternName.text = item
    }
}