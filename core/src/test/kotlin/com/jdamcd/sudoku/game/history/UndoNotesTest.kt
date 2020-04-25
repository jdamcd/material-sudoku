package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class UndoNotesTest {

    private lateinit var game: Game

    @Before
    fun setUp() {
        game = Game(Sudoku(TestPuzzles.VALID))
    }

    @Test
    fun doesNotEnableUndoIfAllNotesUndone() {
        game.toggleNote(0, 0, 5)
        game.toggleNote(8, 8, 5)
        game.undo()
        game.undo()

        assertThat(game.canUndo()).isFalse()
    }

    @Test
    fun enablesUndoIfNoteMade() {
        game.toggleNote(0, 0, 5)

        assertThat(game.canUndo()).isTrue()
    }

    @Test
    fun undoNote() {
        game.toggleNote(0, 0, 5)
        game.undo()

        assertThat(game.hasNote(0, 0, 5)).isFalse()
    }

    @Test
    fun undoNoteToggledOff() {
        game.toggleNote(0, 0, 6)
        game.toggleNote(0, 0, 6)
        game.undo()

        assertThat(game.hasNote(0, 0, 6)).isTrue()
    }

    @Test
    fun returnsPositionOfUndoCell() {
        game.toggleNote(8, 8, 5)

        val (row, col) = game.undo()

        assertThat(row).isEqualTo(8)
        assertThat(col).isEqualTo(8)
    }

    @Test
    fun undoClearNote() {
        game.toggleNote(0, 0, 5)
        game.clear(0, 0)

        game.undo()

        assertThat(game.hasNote(0, 0, 5)).isTrue()
    }

    @Test
    fun undoClearMultipleNotesOnSameCell() {
        game.toggleNote(0, 0, 4)
        game.toggleNote(0, 0, 5)
        game.toggleNote(0, 0, 6)

        game.clear(0, 0)
        game.undo()

        assertThat(game.hasNote(0, 0, 4)).isTrue()
        assertThat(game.hasNote(0, 0, 5)).isTrue()
        assertThat(game.hasNote(0, 0, 6)).isTrue()
    }

    @Test
    fun doesNotUndoWhenNoMovesLeft() {
        game.toggleNote(0, 0, 5)
        game.clear(0, 0)
        game.undo()
        game.undo()

        assertThat(game.hasNote(0, 0, 5)).isFalse()
        assertThat(game.canUndo()).isFalse()
    }
}
