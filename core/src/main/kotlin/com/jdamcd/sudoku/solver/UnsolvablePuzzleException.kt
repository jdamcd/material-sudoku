package com.jdamcd.sudoku.solver

class UnsolvablePuzzleException : Throwable() {

    override val message get() = "No possible solution from set of givens"
}
