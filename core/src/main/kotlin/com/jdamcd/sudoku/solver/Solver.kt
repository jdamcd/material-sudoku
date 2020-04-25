package com.jdamcd.sudoku.solver

interface Solver {

    @Throws(UnsolvablePuzzleException::class)
    fun solve(givens: Array<IntArray>): Array<IntArray>
}
