package com.jdamcd.sudoku.game.history

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class UndoClearTest {

    private lateinit var game: Game

    @Before
    fun setUp() {
        game = Game(Sudoku(TestPuzzles.VALID))
    }

    @Test
    fun undoNote() {
        game.toggleNote(0, 0, 4)

        game.clear(0, 0)
        game.undo()

        assertThat(game.hasNote(0, 0, 4)).isTrue()
    }

    @Test
    fun undoAnswer() {
        game.setAnswer(0, 0, 4)

        game.clear(0, 0)
        game.undo()

        assertThat(game.getAnswer(0, 0)).isEqualTo(4)
    }

    @Test
    fun undoNotesAndAnswerOnSameCell() {
        game.toggleNote(0, 0, 4)
        game.toggleNote(0, 0, 5)
        game.toggleNote(0, 0, 6)
        game.setAnswer(0, 0, 9)

        game.clear(0, 0)
        game.undo()

        assertThat(game.hasNote(0, 0, 4)).isTrue()
        assertThat(game.hasNote(0, 0, 5)).isTrue()
        assertThat(game.hasNote(0, 0, 6)).isTrue()
        assertThat(game.getAnswer(0, 0)).isEqualTo(9)
    }
}
