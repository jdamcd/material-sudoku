package com.jdamcd.sudoku.util

import com.jdamcd.sudoku.TestPuzzles
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ValidateTest {

    private lateinit var validPuzzle: Array<IntArray>
    private lateinit var invalidPuzzle: Array<IntArray>

    @Before
    fun setUp() {
        validPuzzle = Format.gridFromString(TestPuzzles.VALID)
        invalidPuzzle = Format.gridFromString(TestPuzzles.INCONSISTENT)
    }

    @Test
    fun validatesConsistentPuzzle() {
        assertThat(Validate.isValid(validPuzzle)).isTrue()
    }

    @Test
    fun doesNotValidateInconsistentPuzzle() {
        assertThat(Validate.isValid(invalidPuzzle)).isFalse()
    }

    @Test
    fun validatesConsistentColumn() {
        assertThat(Validate.isValidColumn(5, 7, validPuzzle)).isTrue()
    }

    @Test
    fun doesNotValidateInconsistentColumn() {
        val inconsistentCol = Format.gridFromString(TestPuzzles.INCONSISTENT_COL)

        assertThat(Validate.isValidColumn(1, 6, inconsistentCol)).isFalse()
    }

    @Test
    fun validatesConsistentRow() {
        assertThat(Validate.isValidRow(2, 7, validPuzzle)).isTrue()
    }

    @Test
    fun doesNotValidateInconsistentRow() {
        val inconsistentRow = Format.gridFromString(TestPuzzles.INCONSISTENT_ROW)

        assertThat(Validate.isValidRow(1, 0, inconsistentRow)).isFalse()
    }

    @Test
    fun validatesConsistentBox() {
        assertThat(Validate.isValidBox(4, 8, validPuzzle)).isTrue()
    }

    @Test
    fun doesNotValidateInconsistentBox() {
        val inconsistentBox = Format.gridFromString(TestPuzzles.INCONSISTENT_BOX)

        assertThat(Validate.isValidBox(1, 6, inconsistentBox)).isFalse()
    }
}
