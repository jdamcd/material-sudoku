package com.jdamcd.sudoku.game

data class CellPosition(val row: Int, val col: Int) {

    constructor() : this(NOT_SET, NOT_SET)

    fun isSet(): Boolean = row >= 0 && col >= 0

    companion object {
        private const val NOT_SET = -1
    }
}
