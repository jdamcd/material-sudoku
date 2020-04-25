package com.jdamcd.sudoku.game

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.solver.UnsolvablePuzzleException
import com.jdamcd.sudoku.util.Format
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class SudokuTest {

    private lateinit var puzzle: Sudoku

    @Before
    fun setUp() {
        puzzle = Sudoku(TestPuzzles.VALID)
    }

    @Test
    fun getsCellValue() {
        assertThat(puzzle.getCellValue(0, 3)).isEqualTo(4)
        assertThat(puzzle.getCellValue(8, 7)).isEqualTo(2)
    }

    @Test
    fun setsCellValue() {
        puzzle.setCellValue(2, 7, 9)
        puzzle.setCellValue(8, 8, 1)

        assertThat(puzzle.getCellValue(2, 7)).isEqualTo(9)
        assertThat(puzzle.getCellValue(8, 8)).isEqualTo(1)
    }

    @Test
    fun puzzleIsNotSolvedBeforeSolutionIsSet() {
        assertThat(puzzle.isSolution).isFalse()
    }

    @Test
    @Throws(UnsolvablePuzzleException::class)
    fun puzzleIsSolvedAfterCallingSolve() {
        puzzle.solve()

        assertThat(puzzle.isSolution).isTrue()
    }

    @Test
    fun puzzleIsSolvedAfterValidSolutionSet() {
        puzzle.solution = Format.gridFromString(TestPuzzles.VALID_SOLUTION)

        assertThat(puzzle.isSolution).isTrue()
    }

    @Test
    fun getsSolutionCellValue() {
        puzzle.solution = Format.gridFromString(TestPuzzles.VALID_SOLUTION)

        assertThat(puzzle.getSolutionCellValue(0, 4)).isEqualTo(5)
        assertThat(puzzle.getSolutionCellValue(8, 8)).isEqualTo(1)
    }

    @Test
    fun validatesPuzzle() {
        assertThat(puzzle.validate()).isTrue()
    }

    @Test
    fun doesNotValidateInconsistentPuzzle() {
        val invalid = Sudoku(TestPuzzles.INCONSISTENT)

        assertThat(invalid.validate()).isFalse()
    }

    @Test
    fun implementsEquals() {
        EqualsVerifier.forClass(Sudoku::class.java)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify()
    }
}
