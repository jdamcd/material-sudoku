package com.jdamcd.sudoku.solver

import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudoku.util.Format
import java.io.IOException
import java.nio.file.FileSystems
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PackagedPuzzlesTest {

    @Test
    @Throws(IOException::class, UnsolvablePuzzleException::class)
    fun solvesEasyPuzzles() {
        solvePuzzles("easy.sdm")
    }

    @Test
    @Throws(IOException::class, UnsolvablePuzzleException::class)
    fun solvesMediumPuzzles() {
        solvePuzzles("medium.sdm")
    }

    @Test
    @Throws(IOException::class, UnsolvablePuzzleException::class)
    fun solvesHardPuzzles() {
        solvePuzzles("hard.sdm")
    }

    @Test
    @Throws(IOException::class, UnsolvablePuzzleException::class)
    fun solvesExtremePuzzles() {
        solvePuzzles("extreme.sdm")
    }

    @Test
    @Throws(UnsolvablePuzzleException::class)
    fun solvesHeartPuzzle() {
        val puzzle = Sudoku(HEART_PUZZLE)
        puzzle.solve()
        assertSolved(puzzle)
    }

    @Throws(IOException::class, UnsolvablePuzzleException::class)
    private fun solvePuzzles(path: String) {
        val resource = javaClass.getResourceAsStream(FileSystems.getDefault().separator + path)

        val reader = resource.bufferedReader()
        while (true) {
            val line = reader.readLine() ?: break
            val puzzle = Sudoku(line)
            puzzle.solve()
            assertSolved(puzzle)
        }
    }

    private fun assertSolved(puzzle: Sudoku) {
        assertThat(puzzle.solution).isNotNull()
        val solution = Format.arrayFromGrid(puzzle.solution!!)
        assertThat(solution).doesNotContain(0)
    }

    companion object {
        private const val HEART_PUZZLE = "000000000023000780100406009900050004200000008010000030008000300000209000000010000"
    }
}
