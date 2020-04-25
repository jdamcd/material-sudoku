package com.jdamcd.sudoku.solver

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudoku.util.Format
import java.io.IOException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test

class SolverTest {

    @Test
    @Throws(UnsolvablePuzzleException::class)
    fun solveValidPuzzle() {
        val valid = Sudoku(TestPuzzles.VALID)

        valid.solve()

        assertThat(valid.solution).isNotNull()
    }

    @Test(expected = UnsolvablePuzzleException::class)
    @Throws(UnsolvablePuzzleException::class)
    fun throwsForInconsistentPuzzle() {
        val invalid = Sudoku(TestPuzzles.INCONSISTENT)

        invalid.solve()
    }

    @Test(expected = UnsolvablePuzzleException::class)
    @Throws(UnsolvablePuzzleException::class)
    fun throwsForUnsolvablePuzzle() {
        val invalid = Sudoku(TestPuzzles.UNSOLVABLE)

        invalid.solve()
    }

    @Test
    @Throws(UnsolvablePuzzleException::class)
    fun solveBadCasePuzzle() {
        val badCase = Sudoku(TestPuzzles.BRUTE_FORCE_BAD)

        badCase.solve()

        assertThat(badCase.solution).isNotNull()
    }

    @Test
    @Throws(UnsolvablePuzzleException::class)
    fun solveWorstCasePuzzle() {
        val worstCase = Sudoku(TestPuzzles.BRUTE_FORCE_WORST)

        worstCase.solve()

        assertThat(worstCase.solution).isNotNull()
    }

    @Ignore
    @Test
    @Throws(IOException::class, UnsolvablePuzzleException::class)
    fun solvesLargePuzzleSet() {
        val resource = javaClass.getResourceAsStream("/puzzles-10000.sdm")

        val reader = resource.bufferedReader()
        while (true) {
            val line = reader.readLine() ?: break
            val puzzle = Sudoku(line)
            puzzle.solve()

            val solution = Format.arrayFromGrid(puzzle.solution!!)
            assertThat(solution).doesNotContain(0)
        }
    }
}
