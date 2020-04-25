package com.jdamcd.sudoku.game

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CellPositionTest {

    @Test
    fun defaultPositionIsNotSet() {
        val position = CellPosition()

        assertThat(position.isSet()).isFalse()
    }

    @Test
    fun rowAndColumnAreSetWhenCreatedWithParams() {
        val position = CellPosition(3, 6)

        assertThat(position.isSet()).isTrue()
        assertThat(position.row).isEqualTo(3)
        assertThat(position.col).isEqualTo(6)
    }
}
