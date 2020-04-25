package com.jdamcd.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku

class PreviewPuzzleView(context: Context, attrs: AttributeSet) : PuzzleView(context, attrs) {

    private var game: Game? = null

    private lateinit var solved: Paint

    fun setGame(game: Game) {
        this.game = game
        setPuzzle(game.sudoku)
    }

    fun setPreview(sudoku: Sudoku) {
        game = Game(sudoku)
        setPuzzle(sudoku)
    }

    override fun initSizePaints() {
        super.initSizePaints()
        solved = makeSolvedPaint()
    }

    private fun makeSolvedPaint(): Paint {
        val solved = Paint(Paint.ANTI_ALIAS_FLAG)
        solved.color = ContextCompat.getColor(context, R.color.preview_solved)
        solved.style = Style.FILL
        return solved
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun drawDigits(canvas: Canvas) {
        if (game == null) return
        val radius = cellWidth / 6
        val midOffset = cellWidth / 2
        for (row in 0..8) {
            for (col in 0..8) {
                if (game!!.getGiven(row, col) != 0) {
                    canvas.drawCircle(padWidth + col * cellWidth + midOffset, padHeight + row * cellHeight + midOffset, radius, givens)
                } else if (game!!.getAnswer(row, col) != 0) {
                    canvas.drawCircle(padWidth + col * cellWidth + midOffset, padHeight + row * cellHeight + midOffset, radius, solved)
                }
            }
        }
    }
}
