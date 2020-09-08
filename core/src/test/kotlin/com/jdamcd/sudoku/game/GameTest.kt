package com.jdamcd.sudoku.game

import com.jdamcd.sudoku.TestPuzzles
import com.jdamcd.sudoku.util.Format
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GameTest {

    private lateinit var sudoku: Sudoku
    private lateinit var game: Game

    @Before
    fun setUp() {
        sudoku = Sudoku(TestPuzzles.VALID)
        sudoku.solution = Format.gridFromString(TestPuzzles.VALID_SOLUTION)
        game = Game(sudoku)
    }

    @Test
    fun getsGivenPuzzleValues() {
        assertThat(game.getGiven(0, 3)).isEqualTo(4)
        assertThat(game.getGiven(8, 7)).isEqualTo(2)
    }

    @Test
    fun setsAnswerValues() {
        game.setAnswer(2, 7, 9)
        game.setAnswer(8, 8, 1)

        assertThat(game.getAnswer(2, 7)).isEqualTo(9)
        assertThat(game.getAnswer(8, 8)).isEqualTo(1)
    }

    @Test
    fun givenDigitsAreIncludedInAnswers() {
        assertThat(game.getAnswer(0, 3)).isEqualTo(4)
        assertThat(game.getAnswer(8, 7)).isEqualTo(2)
    }

    @Test
    fun cellsWith0AreEmpty() {
        assertThat(game.isEmpty(0, 0)).isTrue()
    }

    @Test
    fun cellsWithGivenDigitAreNotEmpty() {
        assertThat(game.isEmpty(0, 1)).isFalse()
    }

    @Test
    fun puzzleCellsAreGivens() {
        assertThat(game.isGiven(0, 1)).isTrue()
    }

    @Test
    fun answerCellsAreNotGivens() {
        assertThat(game.isGiven(0, 0)).isFalse()
    }

    @Test
    fun answeredCellsAreNotEmpty() {
        game.setAnswer(0, 0, 9)

        assertThat(game.isEmpty(0, 0)).isFalse()
    }

    @Test
    fun gameStartsWithNoCorrectAnswers() {
        assertThat(game.getNumberOfCorrectAnswers()).isZero()
    }

    @Test
    fun gameStarts0PercentCorrect() {
        assertThat(game.getPercentageCorrect()).isZero()
    }

    @Test
    fun completedGameIs100PercentCorrect() {
        val game = Game(sudoku, Format.gridFromString(TestPuzzles.VALID_SOLUTION))

        assertThat(game.getPercentageCorrect()).isEqualTo(100)
    }

    @Test
    fun gameIsNotStartedWithoutAnswers() {
        assertThat(game.isStarted()).isFalse()
    }

    @Test
    fun gameIsStartedAfterFirstAnswer() {
        game.setAnswer(0, 0, 9)

        assertThat(game.isStarted()).isTrue()
    }

    @Test
    fun gameIsCompletedWhenAllCellsAreAnswered() {
        val game = Game(sudoku, Format.gridFromString(TestPuzzles.VALID_SOLUTION))

        assertThat(game.isCompleted()).isTrue()
    }

    @Test
    fun cheatedCellsAreAnswered() {
        game.cheatCell(0, 0)

        assertThat(game.isEmpty(0, 0)).isFalse()
        assertThat(game.getAnswer(0, 0)).isEqualTo(3)
    }

    @Test
    fun randomCheatedCellIsAnswered() {
        val position = game.cheatRandomCell()

        assertThat(game.getAnswer(position.row, position.col))
            .isEqualTo(game.getSolution(position.row, position.col))
    }

    @Test
    fun positionIsNotSetIfRandomCheatIsNotPossible() {
        val game = Game(sudoku, Format.gridFromString(TestPuzzles.VALID_SOLUTION))

        val position = game.cheatRandomCell()

        assertThat(position.isSet()).isFalse()
    }

    @Test
    fun gameIsNotStartedAfterReset() {
        val game = Game(sudoku, Format.gridFromString(TestPuzzles.VALID_SOLUTION))

        game.resetProgress()

        assertThat(game.isCompleted()).isFalse()
        assertThat(game.isStarted()).isFalse()
    }

    @Test
    fun gameStartsWithNoNotes() {
        assertThat(game.hasNotes()).isFalse()
    }

    @Test
    fun gameHasNotesOnceAnyNoteIsSet() {
        game.toggleNote(0, 0, 9)

        assertThat(game.hasNotes()).isTrue()
    }

    @Test
    fun addsSingleNoteToCell() {
        game.toggleNote(8, 8, 5)

        val notes = game.getNotes(8, 8)

        assertThat(notes[0]).isEqualTo(5)
    }

    @Test
    fun removesNoteFromCell() {
        game.toggleNote(8, 8, 3)
        game.toggleNote(8, 8, 3)

        assertThat(game.getNotes(8, 8).size).isZero()
    }

    @Test
    fun hasNoteWhenNoteValueIsSet() {
        game.toggleNote(5, 3, 5)

        assertThat(game.hasNote(5, 3, 5)).isTrue()
    }

    @Test
    fun doesNotHaveNoteIfNoNoteValueIsSet() {
        assertThat(game.hasNote(2, 7, 4)).isFalse()
    }

    @Test
    fun removesSingleNoteFromCellWithMultipleNotes() {
        game.toggleNote(8, 8, 1)
        game.toggleNote(8, 8, 3)
        game.toggleNote(8, 8, 3)

        val notes = game.getNotes(8, 8)

        assertThat(notes.size).isEqualTo(1)
        assertThat(notes[0]).isEqualTo(1)
    }

    @Test
    fun addsAllPossibleNotesToCell() {
        for (i in 1..9) {
            game.toggleNote(2, 2, i)
        }

        val notes = game.getNotes(2, 2)
        for (i in 1..9) {
            assertThat(notes).contains(i)
        }
    }

    @Test
    fun addsMultipleNotesToCell() {
        game.toggleNote(8, 8, 1)
        game.toggleNote(8, 8, 2)

        val notes = game.getNotes(8, 8)

        assertThat(notes.size).isEqualTo(2)
    }

    @Test
    fun clearsAllNotesForCell() {
        game.toggleNote(8, 8, 1)
        game.toggleNote(8, 8, 2)

        game.clear(8, 8)

        assertThat(game.hasNotes(8, 8)).isFalse()
        assertThat(game.getNotes(8, 8).size).isZero()
    }

    @Test
    fun clearsAllNotes() {
        game.toggleNote(0, 0, 1)
        game.toggleNote(8, 8, 9)

        game.clearNotes()

        assertThat(game.hasNotes()).isFalse()
    }

    @Test
    fun clearNoteHistoryWhenNotesAreCleared() {
        game.toggleNote(0, 0, 1)
        game.toggleNote(8, 8, 9)

        game.clearNotes()

        assertThat(game.canUndo()).isFalse()
    }

    @Test
    fun ignoresOtherHistoryWhenNotesAreCleared() {
        game.toggleNote(0, 0, 1)
        game.toggleNote(8, 8, 9)
        game.setAnswer(2, 7, 9)

        game.clearNotes()

        assertThat(game.canUndo()).isTrue()
    }

    @Test
    fun implementsEquals() {
        EqualsVerifier.forClass(Game::class.java)
            .suppress(Warning.NONFINAL_FIELDS)
            .withIgnoredFields("moves")
            .verify()
    }
}
