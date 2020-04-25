package com.jdamcd.sudoku.util

import com.jdamcd.sudoku.TestPuzzles
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FormatTest {

    @Test
    fun formatsGridFromString() {
        val puzzle = Format.gridFromString(TestPuzzles.VALID)

        assertThat(puzzle[8][6]).isEqualTo(8)
        assertThat(puzzle[4][8]).isEqualTo(3)
    }

    @Test
    fun formatsGridFromArray() {
        val puzzle = Format.gridFromArray(TestPuzzles.ARRAY_VALID)

        assertThat(puzzle[8][6]).isEqualTo(8)
        assertThat(puzzle[4][8]).isEqualTo(3)
    }

    @Test
    fun formatsStringFromArray() {
        val puzzle = Format.gridFromArray(TestPuzzles.ARRAY_VALID)
        val puzzleString = Format.stringFromGrid(puzzle)

        assertThat(puzzleString).isEqualTo(TestPuzzles.VALID)
    }

    @Test
    fun makesDeepCopyOfGrid() {
        val puzzle = Format.gridFromString(TestPuzzles.VALID)
        val copy = Format.deepCopy(puzzle)

        puzzle[8][6] = 2

        assertThat(copy[8][6]).isNotEqualTo(2)
    }

    @Test
    fun copyMaintainsGridOrientation() {
        val original = Format.gridFromString(TestPuzzles.VALID)
        assertKnownPositions(original)

        val copy = Format.deepCopy(original)
        assertKnownPositions(copy)
    }

    private fun assertKnownPositions(puzzle: Array<IntArray>) {
        assertThat(puzzle[0][1]).isEqualTo(1)
        assertThat(puzzle[0][2]).isEqualTo(6)
        assertThat(puzzle[8][6]).isEqualTo(8)
        assertThat(puzzle[8][7]).isEqualTo(2)
    }

    @Test
    fun formatsArrayFromGrid() {
        val puzzle = Format.gridFromString(TestPuzzles.VALID)
        val puzzleArray = Format.arrayFromGrid(puzzle)

        assertThat(puzzleArray).isEqualTo(TestPuzzles.ARRAY_VALID)
    }

    @Test
    fun formatsPuzzleStringBothWays() {
        val puzzle = Format.gridFromString(TestPuzzles.VALID)
        val puzzleString = Format.stringFromGrid(puzzle)

        assertThat(puzzleString).isEqualTo(TestPuzzles.VALID)
    }

    @Test
    fun formatsPuzzleAsPrettyString() {
        val puzzle = Format.gridFromString(TestPuzzles.VALID)
        val prettyPuzzle = Format.prettyStringFromGrid(puzzle)

        println(prettyPuzzle)
        assertThat(prettyPuzzle).isEqualTo(TestPuzzles.PRETTY_VALID)
    }
}
