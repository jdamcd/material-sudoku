package com.jdamcd.sudoku.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game
import kotlin.math.max
import kotlin.math.min

class GamePuzzleView(context: Context, attrs: AttributeSet) : PuzzleView(context, attrs) {

    private var game: Game? = null
    private var showMistakes: Boolean = false

    private var hasRestoredState = false
    private var isInteractionEnabled = true

    private lateinit var cellHighlight: Paint
    private lateinit var cellSelected: Paint
    private lateinit var cellError: Paint
    private lateinit var solved: Paint
    private lateinit var mistake: Paint
    private lateinit var notePaint: Paint
    private lateinit var noteBackground: Paint
    private lateinit var noteFont: Paint.FontMetrics

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

    fun setGame(game: Game) {
        this.game = game
        setPuzzle(game.sudoku)
    }

    fun setShowMistakes(showMistakes: Boolean) {
        this.showMistakes = showMistakes
    }

    override fun initPaints() {
        super.initPaints()
        cellSelected = Paint()
        cellSelected.color = ContextCompat.getColor(context, R.color.cell_selected)
        cellError = Paint()
        cellError.color = ContextCompat.getColor(context, R.color.cell_warning)
        noteBackground = Paint()
        noteBackground.color = ContextCompat.getColor(context, R.color.note_background)
        cellHighlight = Paint()
        cellHighlight.color = ContextCompat.getColor(context, R.color.cell_highlight)
    }

    override fun initSizePaints() {
        super.initSizePaints()
        solved = makeDigitPaint(R.color.digits_solved)
        mistake = makeDigitPaint(R.color.mistake)
        notePaint = makeNotesPaint()
        noteFont = notePaint.fontMetrics
    }

    private fun makeDigitPaint(colorRes: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = ContextCompat.getColor(context, colorRes)
        paint.style = Paint.Style.FILL
        paint.textSize = DIGIT_SCALE * cellHeight
        paint.textAlign = Paint.Align.CENTER
        return paint
    }

    private fun makeNotesPaint(): Paint {
        val solved = Paint(Paint.ANTI_ALIAS_FLAG)
        solved.color = ContextCompat.getColor(context, R.color.digits_note)
        solved.style = Paint.Style.FILL
        solved.textSize = NOTE_SIZE * cellHeight
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
        drawHighlights(canvas)
        drawSelectedCell(canvas)
    }

    override fun drawDigits(canvas: Canvas) {
        if (game == null) return
        for (row in 0..8) {
            for (col in 0..8) {
                drawCellDigit(game!!, canvas, row, col)
            }
        }
    }

    private fun drawCellDigit(game: Game, canvas: Canvas, row: Int, col: Int) {
        if (game.getGiven(row, col) != 0) {
            drawDigit(canvas, givens, row, col, game.getGiven(row, col))
        } else if (showMistakes && game.getAnswer(row, col) != 0 &&
            game.getAnswer(row, col) != game.getSolution(row, col)
        ) {
            drawDigit(canvas, mistake, row, col, game.getAnswer(row, col))
        } else if (game.getAnswer(row, col) != 0) {
            drawDigit(canvas, solved, row, col, game.getAnswer(row, col))
        } else if (game.hasNotes(row, col)) {
            for (note in game.getNotes(row, col)) {
                drawNote(canvas, row, col, note)
            }
        }
    }

    private fun drawDigit(canvas: Canvas, paint: Paint, row: Int, col: Int, digit: Int) {
        canvas.drawText(
            digit.toString(),
            padWidth + col * cellWidth + digitX,
            padHeight + row * cellHeight + digitY,
            paint
        )
    }

    private fun drawSelectedCell(canvas: Canvas) {
        if (puzzleData != null && selectedRect != null) {
            canvas.drawRect(selectedRect!!, cellSelected)
        }
    }

    private fun drawNote(canvas: Canvas, row: Int, col: Int, value: Int) {
        val totalW = if (isBorderless) (right(col) - left(col)).toFloat() else cellWidth
        val totalH = if (isBorderless) (bottom(row) - top(row)).toFloat() else cellHeight

        val noteAreaX = totalW / 3
        val noteAreaY = totalH / 3
        val noteTextX = totalW / 6
        val noteTextY = totalH / 6 - (noteFont.ascent + noteFont.descent) / 2

        val positionX = left(col) + noteAreaX * ((value - 1) % 3)
        val positionY = top(row) + noteAreaY * ((value - 1) / 3)

        canvas.drawRect(positionX, positionY, positionX + noteAreaX, positionY + noteAreaY, noteBackground)
        canvas.drawText(value.toString(), positionX + noteTextX, positionY + noteTextY, notePaint)
    }

    private fun drawHighlights(canvas: Canvas) {
        if (puzzleData == null) return
        for (row in 0..8) {
            for (col in 0..8) {
                if (game!!.hasAnswer(row, col)) {
                    highlightInconsistency(canvas, row, col)
                    highlightSelection(canvas, selectedDigit, row, col)
                }
            }
        }
    }

    private fun highlightInconsistency(canvas: Canvas, row: Int, col: Int) {
        if (!game!!.isValidBox(row, col)) {
            canvas.drawRect(getBoxRect(row, col), cellError)
        } else {
            if (!game!!.isValidRow(row, col)) {
                canvas.drawRect(getRowRect(row), cellError)
            }
            if (!game!!.isValidColumn(row, col)) {
                canvas.drawRect(getColRect(col), cellError)
            }
        }
    }

    private fun highlightSelection(canvas: Canvas, selectedDigit: Int, row: Int, col: Int) {
        if (selectedRect != null && game!!.getAnswer(row, col) == selectedDigit) {
            canvas.drawRect(getCellRect(row, col), cellHighlight)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isInteractionEnabled) {
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
        isInteractionEnabled = enabled
        if (!isInteractionEnabled) {
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

    private val selectedDigit: Int
        get() = if (isCursorSet()) {
            game!!.getAnswer(cursorRow, cursorCol)
        } else NOT_SET

    val cursorPosition: CellPosition
        get() = if (selectedRect == null) {
            CellPosition()
        } else CellPosition(cursorRow, cursorCol)

    private fun isCursorSet() = cursorRow >= 0 && cursorCol >= 0

    private fun selectPressedCell(row: Int, col: Int) {
        if (puzzleData == null) return
        cursorRow = min(max(row, 0), 8)
        cursorCol = min(max(col, 0), 8)
        selectedRect = getCellRect(cursorRow, cursorCol)
        invalidate()
        notifyListener()
    }

    fun setCursor(row: Int, col: Int) {
        cursorRow = row
        cursorCol = col
        selectedRect = getCellRect(row, col)
        invalidate()
    }

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

    override fun onSaveInstanceState(): Parcelable {
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
        private const val NOT_SET = -1
        private const val NOTE_SIZE = 0.25f
        private const val STATE_SUPER = "state_super"
        private const val STATE_CURSOR = "state_cursor"
    }
}
