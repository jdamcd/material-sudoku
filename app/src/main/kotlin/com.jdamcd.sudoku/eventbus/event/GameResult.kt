package com.jdamcd.sudoku.eventbus.event

import com.jdamcd.sudoku.repository.Level

data class GameResult(
    val level: Level,
    val time: Long,
    val cheats: Int
)
