package com.jdamcd.sudoku.util

object Validate {

    fun isValid(puzzle: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (puzzle[row][col] != 0) {
                    if (!isValidRow(row, col, puzzle) || !isValidColumn(row, col, puzzle) || !isValidBox(row, col, puzzle)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun isValidRow(row: Int, col: Int, puzzle: Array<IntArray>): Boolean {
        val value = puzzle[row][col]
        return (0..8).none { value == puzzle[row][it] && it != col }
    }

    fun isValidColumn(row: Int, col: Int, puzzle: Array<IntArray>): Boolean {
        val value = puzzle[row][col]
        return (0..8).none { value == puzzle[it][col] && it != row }
    }

    fun isValidBox(row: Int, col: Int, puzzle: Array<IntArray>): Boolean {
        val value = puzzle[row][col]
        val offsetRow = row / 3 * 3
        val offsetCol = col / 3 * 3
        for (subRow in 0..2) {
            for (subCol in 0..2) {
                val boxRow = offsetRow + subRow
                val boxCol = offsetCol + subCol
                if (value == puzzle[boxRow][boxCol] && !(boxRow == row && boxCol == col)) {
                    return false
                }
            }
        }
        return true
    }
}
