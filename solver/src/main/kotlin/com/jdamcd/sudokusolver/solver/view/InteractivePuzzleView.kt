package com.jdamcd.sudokusolver.solver.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudokusolver.R

class InteractivePuzzleView(context: Context, attrs: AttributeSet) : PuzzleView(context, attrs) {

    private var hasRestoredState = false
    private var interactionEnabled = true

    private lateinit var cellSelected: Paint
    private lateinit var cellError: Paint
    private lateinit var solved: Paint

    private var selectedRect: Rect? = null
    private var cursorCol = NOT_SET
    private var cursorRow = NOT_SET

    private var listener: OnCellSelectedListener? = null

    interface OnCellSelectedListener {
        fun onCellSelected(position: CellPosition)
    }

    init {
        isSaveEnabled = true
        isFocusable = true
        isSelected = true
        isClickable = true
    }

    override fun initPaints() {
        super.initPaints()
        cellSelected = Paint()
        cellSelected.color = ContextCompat.getColor(context, R.color.cell_selected)
        cellError = Paint()
        cellError.color = ContextCompat.getColor(context, R.color.cell_warning)
    }

    override fun initSizePaints() {
        super.initSizePaints()
        solved = makeSolvedPaint()
    }

    private fun makeSolvedPaint(): Paint {
        val solved = Paint(Paint.ANTI_ALIAS_FLAG)
        solved.color = ContextCompat.getColor(context, R.color.digits_solved)
        solved.style = Paint.Style.FILL
        solved.textSize = DIGIT_SIZE * cellHeight
        solved.textAlign = Paint.Align.CENTER
        return solved
    }

    override fun drawUnderDigits(canvas: Canvas) {
        if (hasRestoredState) {
            setCursor(cursorRow, cursorCol)
            hasRestoredState = false
            if (isCursorSet()) {
                notifyListener()
            }
        }
        drawWarnings(canvas)
        drawSelectedCell(canvas)
    }

    override fun drawDigits(canvas: Canvas) {
        super.drawDigits(canvas)
        if (puzzleData == null || !puzzleData!!.isSolution) return
        for (row in 0..8) {
            for (col in 0..8) {
                if (puzzleData!!.getCellValue(row, col) == 0) {
                    canvas.drawText(
                        puzzleData!!.getSolutionCellValue(row, col).toString() + "",
                        padWidth + col * cellWidth + digitX,
                        padHeight + row * cellHeight + digitY,
                        solved
                    )
                }
            }
        }
    }

    private fun drawWarnings(canvas: Canvas) {
        if (puzzleData == null) return

        for (row in 0..8) {
            for (col in 0..8) {
                if (puzzleData!!.getCellValue(row, col) != 0) {
                    if (!puzzleData!!.isValidBox(row, col)) {
                        canvas.drawRect(getBoxRect(row, col), cellError)
                    } else {
                        if (!puzzleData!!.isValidRow(row, col)) {
                            canvas.drawRect(getRowRect(row), cellError)
                        }
                        if (!puzzleData!!.isValidColumn(row, col)) {
                            canvas.drawRect(getColRect(col), cellError)
                        }
                    }
                }
            }
        }
    }

    private fun drawSelectedCell(canvas: Canvas) {
        if (puzzleData != null && selectedRect != null) {
            canvas.drawRect(selectedRect!!, cellSelected)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!interactionEnabled) {
            return super.onTouchEvent(event)
        }
        if (event.action != MotionEvent.ACTION_DOWN && event.action != MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(event)
        }
        val pressX = (event.x / cellWidth).toInt()
        val pressY = (event.y / cellHeight).toInt()
        if (isDifferentCell(pressY, pressX) && isValidCell(pressY, pressX)) {
            selectPressedCell(pressY, pressX)
            return true
        }
        return false
    }

    override fun setEnabled(enabled: Boolean) {
        interactionEnabled = enabled
        if (!interactionEnabled) {
            clearCursor()
            invalidate()
        }
    }

    private fun isValidCell(pressY: Int, pressX: Int): Boolean {
        return pressX > -1 && pressX < 9 && pressY > -1 && pressY < 9
    }

    private fun isDifferentCell(pressY: Int, pressX: Int): Boolean {
        return !(pressX == cursorCol && pressY == cursorRow)
    }

    private fun selectPressedCell(row: Int, col: Int) {
        if (selectedRect != null) {
            invalidate()
        }
        cursorRow = Math.min(Math.max(row, 0), 8)
        cursorCol = Math.min(Math.max(col, 0), 8)
        selectedRect = getCellRect(cursorRow, cursorCol)
        invalidate()
        notifyListener()
    }

    fun getCursorPosition(): CellPosition =
        if (selectedRect == null) CellPosition()
        else CellPosition(cursorRow, cursorCol)

    private fun setCursor(row: Int, col: Int) {
        cursorRow = row
        cursorCol = col
        selectedRect = getCellRect(row, col)
        invalidate()
    }

    private fun isCursorSet() = cursorRow >= 0 && cursorCol >= 0

    fun clearCursor() {
        cursorRow = NOT_SET
        cursorCol = NOT_SET
        if (selectedRect != null) {
            invalidate()
            selectedRect = null
        }
    }

    private fun getCellRect(row: Int, col: Int): Rect {
        return Rect(left(col), top(row), right(col), bottom(row))
    }

    private fun getRowRect(row: Int): Rect {
        return Rect(0, top(row), width, bottom(row))
    }

    private fun getColRect(col: Int): Rect {
        return Rect(left(col), 0, right(col), height)
    }

    private fun getBoxRect(row: Int, col: Int): Rect {
        // Get top left cell of box with int division
        val boxX = col / 3 * 3
        val boxY = row / 3 * 3
        return Rect(left(boxX), top(boxY), right(boxX + 2), bottom(boxY + 2))
    }

    private fun left(col: Int): Int {
        return if (col == 0) 0 else (col * cellWidth + padWidth).toInt()
    }

    private fun right(col: Int): Int {
        return if (col == 8) width else (col * cellWidth + cellWidth + padWidth).toInt()
    }

    private fun top(row: Int): Int {
        return if (row == 0) 0 else (row * cellHeight + padHeight).toInt()
    }

    private fun bottom(row: Int): Int {
        return if (row == 8) height else (row * cellHeight + cellHeight + padHeight).toInt()
    }

    fun setOnCellSelectedListener(listener: OnCellSelectedListener) {
        this.listener = listener
    }

    private fun notifyListener() = listener?.onCellSelected(CellPosition(cursorRow, cursorCol))

    override fun onSaveInstanceState(): Parcelable? {
        val state = Bundle()
        state.putParcelable(STATE_SUPER, super.onSaveInstanceState())
        state.putIntArray(STATE_CURSOR, if (selectedRect == null) intArrayOf(NOT_SET, NOT_SET) else intArrayOf(cursorRow, cursorCol))
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
            val cursor = state.getIntArray(STATE_CURSOR)
            cursorRow = cursor?.get(0) ?: NOT_SET
            cursorCol = cursor?.get(1) ?: NOT_SET
            hasRestoredState = true
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val DIGIT_SIZE = 0.7f
        private const val NOT_SET = -1
        private const val STATE_SUPER = "state_super"
        private const val STATE_CURSOR = "state_cursor"
    }
}
