package com.jdamcd.sudoku.repository

import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku

data class Puzzle(
    val id: Long,
    val level: Level,
    val number: Int,
    val title: String,
    val puzzle: Sudoku,
    val game: Game,
    val solution: String,
    val time: Long,
    val isBookmarked: Boolean,
    val isCompleted: Boolean,
    val numberOfCheats: Int
)
