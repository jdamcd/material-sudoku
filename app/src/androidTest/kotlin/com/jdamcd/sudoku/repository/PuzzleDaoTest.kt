package com.jdamcd.sudoku.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jdamcd.sudoku.DatabaseTest
import com.jdamcd.sudoku.repository.database.PuzzleLoad
import com.jdamcd.sudoku.repository.database.PuzzleSave
import com.jdamcd.sudoku.util.Strings
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PuzzleDaoTest : DatabaseTest() {

    @Test
    fun loadsSinglePuzzle() {
        val actual = dao.getPuzzle(25L).blockingGet()

        val expected = PuzzleLoad(
            25L,
            "easy",
            25,
            "005406700060090030304000506900060005030109060200040008409000601010070050002608900",
            "125436789867591234394782516941867325538129467276345198489253671613974852752618943",
            Strings.EMPTY,
            Strings.EMPTY,
            0,
            false,
            0,
            false,
            0
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun loadsAllPuzzlesForLevel() {
        val actual = dao.getPuzzles("hard").blockingFirst()

        assertThat(actual).hasSize(400)
        assertThat(actual).extracting("level").containsOnly("hard")
    }

    @Test
    fun loadsSetOfPuzzlesById() {
        val ids = setOf(1L, 123L, 1500L)

        val actual = dao.getPuzzles(ids)

        assertThat(actual).extracting("id").containsExactlyElementsOf(ids)
    }

    @Test
    fun loadsPuzzlesByIdInSingleChunkTransaction() {
        val ids = setOf(1L, 123L, 1500L)

        val actual = dao.bulkGetPuzzles(ids)

        assertThat(actual).hasSize(3)
    }

    @Test
    fun loadsPuzzlesByIdInMultiChunkTransaction() {
        val ids = mutableSetOf<Long>()
        for (i in 1L..1001L) ids.add(i)

        val actual = dao.bulkGetPuzzles(ids)

        assertThat(actual).hasSize(1001)
    }

    @Test
    fun loadsBookmarkedPuzzles() {
        dao.updateBookmark(123L, true)
        dao.updateBookmark(1234L, true)

        val actual = dao.getBookmarkedPuzzles().blockingFirst()

        assertThat(actual).extracting("id").containsExactly(123L, 1234L)
    }

    @Test
    fun loadsCompletedPuzzles() {
        saveCompletedPuzzles()

        val actual = dao.getCompletedPuzzles().blockingFirst()

        assertThat(actual).extracting("id").containsExactly(1L, 1500L)
    }

    @Test
    fun loadsCompletedPuzzleCount() {
        saveCompletedPuzzles()

        val actual = dao.countCompleted().blockingGet()

        assertThat(actual).isEqualTo(2)
    }

    @Test
    fun loadsIncompletedPuzzles() {
        saveCompletedPuzzles()

        val actual = dao.getIncompletePuzzles("easy").blockingGet()

        assertThat(actual).hasSize(399)
        assertThat(actual).extracting("id").doesNotContain(1L)
    }

    @Test
    fun removesAllBookmarks() {
        dao.updateBookmark(123L, true)

        dao.removeAllBookmarks()
        val actual = dao.getBookmarkedPuzzles().blockingFirst()

        assertThat(actual).isEmpty()
    }

    @Test
    fun updatesPuzzle() {
        dao.updatePuzzle(25L, "test-game", "test-notes", 1234, true, 20, false, 1)

        val actual = dao.getPuzzle(25L).blockingGet()

        val expected = PuzzleLoad(
            25L,
            "easy",
            25,
            "005406700060090030304000506900060005030109060200040008409000601010070050002608900",
            "125436789867591234394782516941867325538129467276345198489253671613974852752618943",
            "test-game",
            "test-notes",
            1234,
            true,
            20,
            false,
            1
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun bulkInsertsPuzzles() {
        val saves = setOf(
            PuzzleSave.forCompleted(1L, "test1", 123L, 0),
            PuzzleSave.forCompleted(12L, "test2", 456L, 3),
            PuzzleSave.forCompleted(123L, "test3", 1234L, 0)
        )
        dao.bulkUpdatePuzzles(saves)

        val actual = dao.getCompletedPuzzles().blockingFirst()

        assertThat(actual).hasSize(3)
    }

    @Test
    fun updatesBookmark() {
        dao.updateBookmark(123L, true)

        val actual = dao.getPuzzle(123L).blockingGet()

        assertThat(actual.bookmarked).isTrue()
    }

    private fun saveCompletedPuzzles() {
        dao.updatePuzzle(1L, null, null, 1234, false, 100, true, 0)
        dao.updatePuzzle(1500L, null, null, 5678, false, 100, true, 2)
    }
}
