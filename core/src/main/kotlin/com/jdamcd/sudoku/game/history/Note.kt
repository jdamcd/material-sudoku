package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game

data class Note(override val position: CellPosition, private val oldValue: Int) : Move {

    override fun undo(game: Game) {
        game.notes[position.row][position.col] = oldValue
    }
}
