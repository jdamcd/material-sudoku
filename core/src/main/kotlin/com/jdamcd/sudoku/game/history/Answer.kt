package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game

data class Answer(override val position: CellPosition, private val oldValue: Int) : Move {

    override fun undo(game: Game) {
        game.answers[position.row][position.col] = oldValue
    }
}
