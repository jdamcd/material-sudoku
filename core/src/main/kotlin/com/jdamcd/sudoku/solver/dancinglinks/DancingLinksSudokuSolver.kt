package com.jdamcd.sudoku.solver.dancinglinks

import com.jdamcd.sudoku.solver.Solver
import com.jdamcd.sudoku.solver.UnsolvablePuzzleException
import java.util.ArrayList

class DancingLinksSudokuSolver : Solver {

    private lateinit var arena: DancingLinksArena

    private fun initialiseArena(givens: Array<IntArray>): Boolean {
        // Create sparse matrix representing cell position, row, column and box constraints

        val rowData = IntArray(4)
        val givenList = ArrayList<Node>()

        val labels = IntArray(COLUMNS)
        for (i in 0 until COLUMNS) {
            labels[i] = i + 1
        }
        arena = DancingLinksArena(labels)

        for (row in 0..8) {
            for (column in 0..8) {
                for (digit in 0..8) {
                    // Check if cell has digit, so row can be removed
                    val isGiven = givens[row][column] == digit + 1

                    val boxRow = row / 3
                    val boxCol = column / 3
                    // 4 constraint columns
                    rowData[0] = 1 + (row * 9 + column)
                    rowData[1] = 1 + CELLS + (row * 9 + digit)
                    rowData[2] = 1 + CELLS + CELLS + (column * 9 + digit)
                    rowData[3] = 1 + CELLS + CELLS + CELLS + ((boxRow * 3 + boxCol) * 9 + digit)

                    val newRow = arena.addInitialRow(rowData) // Add to sparse matrix
                    if (isGiven) {
                        givenList.add(newRow!!) // Add to constraints
                    }
                }
            }
        }

        // Remove givens - returns consistency
        return arena.removeInitialSolutionSet(givenList)
    }

    @Throws(UnsolvablePuzzleException::class)
    private fun findSolution(rowIndex: IntArray?): Array<IntArray> {
        if (rowIndex == null) {
            throw UnsolvablePuzzleException()
        }

        val solution = Array(9) { IntArray(9) }

        for (i in 0 until CELLS) {
            var value = rowIndex[i]
            val digit: Int
            val row: Int
            val column: Int

            digit = value % 9
            value /= 9
            column = value % 9
            value /= 9
            row = value % 9

            solution[row][column] = digit + 1
        }
        return solution
    }

    @Throws(UnsolvablePuzzleException::class)
    override fun solve(givens: Array<IntArray>): Array<IntArray> {
        if (initialiseArena(givens)) {
            return findSolution(arena.solve())
        } else {
            throw UnsolvablePuzzleException()
        }
    }

    companion object {
        private const val CELLS = 81
        private const val COLUMNS = 324
    }
}
