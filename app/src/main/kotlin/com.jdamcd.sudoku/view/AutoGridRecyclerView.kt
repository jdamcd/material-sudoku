package com.jdamcd.sudoku.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class AutoGridRecyclerView : RecyclerView {

    private var manager = GridLayoutManager(context, 2)
    private var columnWidth = -1
    private var scrollState: Parcelable? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val attrsArray = intArrayOf(android.R.attr.columnWidth)
            val array = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, columnWidth)
            array.recycle()
        }
        layoutManager = manager
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = Bundle()
        state.putParcelable(STATE_KEY_SUPER, super.onSaveInstanceState())
        state.putParcelable(STAT_KEY_SCROLL, layoutManager?.onSaveInstanceState())
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            scrollState = state.getParcelable(STAT_KEY_SCROLL)
            super.onRestoreInstanceState(state.getParcelable(STATE_KEY_SUPER))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        restorePosition()
    }

    private fun restorePosition() {
        if (scrollState != null) {
            layoutManager?.onRestoreInstanceState(scrollState)
            scrollState = null
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (columnWidth > 0) {
            manager.spanCount = max(2, measuredWidth / columnWidth)
        }
    }

    companion object {
        const val STATE_KEY_SUPER = "superState"
        const val STAT_KEY_SCROLL = "scrollState"
    }
}
