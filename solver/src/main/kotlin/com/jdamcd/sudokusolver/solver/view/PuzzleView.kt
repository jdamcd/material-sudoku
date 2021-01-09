package com.jdamcd.sudokusolver.solver.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudokusolver.R
import kotlin.math.max
import kotlin.math.min

abstract class PuzzleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    protected var puzzleData: Sudoku? = null

    protected var cellWidth: Float = 0.toFloat()
    protected var cellHeight: Float = 0.toFloat()
    protected var padWidth: Float = 0.toFloat()
    protected var padHeight: Float = 0.toFloat()
    protected var digitX: Float = 0.toFloat()
    protected var digitY: Float = 0.toFloat()
    private var lineStroke: Float = 0.toFloat()
    private var boxStroke: Float = 0.toFloat()

    private var isBorderless: Boolean = false

    protected lateinit var background: Paint
    private lateinit var givens: Paint
    private lateinit var lines: Paint

    init {
        initPaints()
        setAttributes(context, attrs)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.PuzzleView, 0, 0)
        try {
            lineStroke = attributes.getDimension(R.styleable.PuzzleView_lineStroke, 1f)
            boxStroke = attributes.getDimension(R.styleable.PuzzleView_boxStroke, 2f)
            isBorderless = attributes.getBoolean(R.styleable.PuzzleView_borderless, false)
        } finally {
            attributes.recycle()
        }
    }

    fun setPuzzle(puzzle: Sudoku) {
        this.puzzleData = puzzle
        invalidate()
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val smallestSide = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(smallestSide, smallestSide)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        cellHeight = (height / 9).toFloat()
        cellWidth = (width / 9).toFloat()
        padWidth = (width % 9 / 2).toFloat()
        padHeight = (height % 9 / 2).toFloat()
        initSizePaints()
    }

    protected open fun initPaints() {
        background = Paint()
        background.color = ContextCompat.getColor(context, R.color.board_background)
        lines = Paint()
        lines.color = ContextCompat.getColor(context, R.color.board_lines)
    }

    protected open fun initSizePaints() {
        givens = makeGivensPaint()
        val font = givens.fontMetrics
        digitX = cellWidth / 2
        digitY = cellHeight / 2 - (font.ascent + font.descent) / 2
    }

    private fun makeGivensPaint(): Paint {
        val givens = Paint(Paint.ANTI_ALIAS_FLAG)
        givens.color = ContextCompat.getColor(context, R.color.digits_given)
        givens.style = Style.FILL
        givens.textSize = DIGIT_SIZE * cellHeight
        givens.textAlign = Paint.Align.CENTER
        return givens
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), background)
        drawUnderDigits(canvas)
        drawDigits(canvas)
        drawGridLines(canvas)
        drawBoxLines(canvas)
        if (!isBorderless) {
            drawHorizontalPadding(canvas)
            drawVerticalPadding(canvas)
        }
    }

    open fun drawUnderDigits(canvas: Canvas) {}

    private fun drawGridLines(canvas: Canvas) {
        lines.strokeWidth = lineStroke
        for (i in 1..8) {
            canvas.drawLine(0f, padHeight + i * cellHeight, width.toFloat(), padHeight + i * cellHeight, lines)
            canvas.drawLine(padWidth + i * cellWidth, 0f, padWidth + i * cellWidth, height.toFloat(), lines)
        }
    }

    private fun drawBoxLines(canvas: Canvas) {
        lines.strokeWidth = boxStroke
        var i = 3
        while (i <= 6) {
            canvas.drawLine(0f, padHeight + i * cellHeight, width.toFloat(), padHeight + i * cellHeight, lines)
            canvas.drawLine(padWidth + i * cellWidth, 0f, padWidth + i * cellWidth, height.toFloat(), lines)
            i += 3
        }
    }

    private fun drawVerticalPadding(canvas: Canvas) {
        lines.strokeWidth = max(padWidth * 2, 2f)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), lines)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), lines)
    }

    private fun drawHorizontalPadding(canvas: Canvas) {
        lines.strokeWidth = max(padHeight * 2, 2f)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, lines)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), lines)
    }

    protected open fun drawDigits(canvas: Canvas) {
        if (puzzleData == null) return
        for (row in 0..8) {
            for (col in 0..8) {
                if (puzzleData!!.getCellValue(row, col) != 0) {
                    canvas.drawText(
                        puzzleData!!.getCellValue(row, col).toString(),
                        padWidth + col * cellWidth + digitX,
                        padHeight + row * cellHeight + digitY,
                        givens
                    )
                }
            }
        }
    }

    companion object {
        private const val DIGIT_SIZE = 0.7f
    }
}
