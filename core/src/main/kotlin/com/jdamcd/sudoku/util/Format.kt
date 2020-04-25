package com.jdamcd.sudoku.util

object Format {

    private const val CHAR_ZERO = 48

    fun gridFromString(givens: String): Array<IntArray> {
        val givensArray = IntArray(81)
        var i = 0
        for (value in givens.toCharArray()) {
            givensArray[i++] = value.toInt() - CHAR_ZERO
        }
        return gridFromArray(givensArray)
    }

    fun gridFromArray(givens: IntArray): Array<IntArray> {
        val grid = Array(9) { IntArray(9) }
        for (i in 0..8) {
            System.arraycopy(givens, i * 9, grid[i], 0, 9)
        }
        return grid
    }

    fun deepCopy(givens: Array<IntArray>): Array<IntArray> {
        val copy = Array(9) { IntArray(9) }
        for (row in 0..8) {
            System.arraycopy(givens[row], 0, copy[row], 0, 9)
        }
        return copy
    }

    fun arrayFromGrid(grid: Array<IntArray>): IntArray {
        val out = IntArray(81)
        for (i in 0..8) {
            System.arraycopy(grid[i], 0, out, i * 9, 9)
        }
        return out
    }

    fun stringFromGrid(puzzle: Array<IntArray>): String {
        val output = StringBuilder()
        for (row in 0..8) {
            for (col in 0..8) {
                output.append(puzzle[row][col])
            }
        }
        return output.toString()
    }

    fun serialiseNotes(notes: Array<IntArray>): String {
        val output = StringBuilder()
        for (row in 0..8) {
            for (col in 0..8) {
                output.append(notes[row][col]).append(",")
            }
        }
        return output.toString()
    }

    fun deserialiseNotes(serialised: String): Array<IntArray> {
        val notesArray = IntArray(81)
        val notes = serialised.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i = 0
        for (note in notes) {
            notesArray[i++] = Integer.valueOf(note)!!
        }
        return gridFromArray(notesArray)
    }

    fun prettyStringFromGrid(puzzle: Array<IntArray>): String {
        val builder = StringBuilder()
        var rowNumber = 1
        var colNumber = 1

        for (row in puzzle) {
            for (value in row) {
                if (rowNumber % 4 == 0) {
                    builder.append("-----+-----+-----\n")
                    rowNumber++
                }
                builder.append(if (value != 0) value else " ")
                builder.append(if (colNumber % 3 == 0 && colNumber % 9 != 0) "|" else " ")
                if (colNumber++ % 9 == 0) {
                    builder.append("\n")
                    rowNumber++
                }
            }
        }
        return builder.toString()
    }
}
