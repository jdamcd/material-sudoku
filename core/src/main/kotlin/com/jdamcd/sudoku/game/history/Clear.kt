package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game

data class Clear(override val position: CellPosition, private val oldValue: Int, private var oldNote: Int) : Move {

    fun hasOldValue(): Boolean = oldValue != 0

    fun removeOldNote() {
        oldNote = 0
    }

    override fun undo(game: Game) {
        game.answers[position.row][position.col] = oldValue
        game.notes[position.row][position.col] = oldNote
    }
}
