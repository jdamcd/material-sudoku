package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game

interface Move {

    val position: CellPosition

    fun undo(game: Game)
}
