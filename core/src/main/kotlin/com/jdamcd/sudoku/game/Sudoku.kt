package com.jdamcd.sudoku.game

import com.jdamcd.sudoku.solver.UnsolvablePuzzleException
import com.jdamcd.sudoku.solver.dancinglinks.DancingLinksSudokuSolver
import com.jdamcd.sudoku.util.Format
import com.jdamcd.sudoku.util.Validate

class Sudoku {

    val givens: Array<IntArray>
    var solution: Array<IntArray>? = null

    val isSolution: Boolean
        get() = solution != null

    constructor() {
        givens = Array(9) { IntArray(9) }
    }

    constructor(givens: String) {
        this.givens = Format.gridFromString(givens)
    }

    constructor(givens: IntArray) {
        this.givens = Format.gridFromArray(givens)
    }

    fun getCellValue(row: Int, col: Int): Int = givens[row][col]

    fun setCellValue(row: Int, col: Int, value: Int) {
        givens[row][col] = value
    }

    @Throws(UnsolvablePuzzleException::class)
    fun solve() {
        solution = DancingLinksSudokuSolver().solve(givens)
    }

    fun setSolution(solution: IntArray) {
        this.solution = Format.gridFromArray(solution)
    }

    fun getSolutionCellValue(row: Int, col: Int): Int = solution!![row][col]

    fun validate(): Boolean = Validate.isValid(givens)

    fun isValidColumn(row: Int, col: Int): Boolean = Validate.isValidColumn(row, col, givens)

    fun isValidRow(row: Int, col: Int): Boolean = Validate.isValidRow(row, col, givens)

    fun isValidBox(row: Int, col: Int): Boolean = Validate.isValidBox(row, col, givens)

    override fun toString(): String = Format.prettyStringFromGrid(givens)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Sudoku

        val equalSolution = (solution == null && other.solution == null) ||
            (solution != null && other.solution != null && solution!!.contentDeepEquals(other.solution!!))

        return givens contentDeepEquals other.givens && equalSolution
    }

    override fun hashCode(): Int {
        var result = givens.contentDeepHashCode()
        result = 31 * result + (solution?.contentDeepHashCode() ?: 0)
        return result
    }
}
