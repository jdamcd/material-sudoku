package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class UndoTest {

    private lateinit var game: Game

    @Before
    fun setUp() {
        game = Game(Sudoku(TestPuzzles.VALID))
    }

    @Test
    fun doesNotEnableUndoIfNoMovesMade() {
        assertThat(game.canUndo()).isFalse()
    }

    @Test
    fun doesNotEnableUndoIfAllMovesUndone() {
        game.setAnswer(0, 0, 5)
        game.setAnswer(8, 8, 5)
        game.undo()
        game.undo()

        assertThat(game.canUndo()).isFalse()
    }

    @Test
    fun enablesUndoIfMovesMade() {
        game.setAnswer(0, 0, 5)

        assertThat(game.canUndo()).isTrue()
    }

    @Test
    fun undoMove() {
        game.setAnswer(0, 0, 5)
        game.undo()

        assertThat(game.getAnswer(0, 0)).isZero()
    }

    @Test
    fun undoMovesOnSameCell() {
        game.setAnswer(0, 0, 5)
        game.setAnswer(0, 0, 6)
        game.undo()

        assertThat(game.getAnswer(0, 0)).isEqualTo(5)
    }

    @Test
    fun returnPositionOfUndoCell() {
        game.setAnswer(8, 8, 5)

        val (row, col) = game.undo()

        assertThat(row).isEqualTo(8)
        assertThat(col).isEqualTo(8)
    }
}
