package com.jdamcd.sudoku.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class OffsetDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

    constructor(@NonNull context: Context, @DimenRes offsetId: Int) : this(context.resources.getDimensionPixelSize(offsetId))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(offset, offset, offset, offset)
    }
}
