package com.jdamcd.sudoku.repository.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class PuzzleDao {

    @Query("SELECT * FROM puzzles WHERE _id = :id")
    abstract fun getPuzzle(id: Long): Single<PuzzleLoad>

    @Query("SELECT * FROM puzzles WHERE level = :level")
    abstract fun getPuzzles(level: String): Flowable<List<PuzzleLoad>>

    @Query("SELECT * FROM puzzles WHERE level = :level AND completed != 1")
    abstract fun getIncompletePuzzles(level: String): Single<List<PuzzleLoad>>

    @Query("SELECT * FROM puzzles WHERE bookmarked = 1")
    abstract fun getBookmarkedPuzzles(): Flowable<List<PuzzleLoad>>

    @Query("SELECT * FROM puzzles WHERE completed = 1")
    abstract fun getCompletedPuzzles(): Flowable<List<PuzzleLoad>>

    @Query("SELECT count(*) FROM puzzles WHERE completed = 1")
    abstract fun countCompleted(): Single<Int>

    @Query("SELECT * FROM puzzles WHERE _id in(:ids)")
    abstract fun getPuzzles(ids: Set<Long>): List<PuzzleLoad>

    @Query("UPDATE puzzles SET game=:game, notes=:notes, time=:time, bookmarked=:bookmarked, progress=:progress, completed=:completed, cheats=:cheats WHERE _id=:id")
    abstract fun updatePuzzle(id: Long, game: String?, notes: String?, time: Long, bookmarked: Boolean, progress: Int, completed: Boolean, cheats: Int): Int

    @Query("UPDATE puzzles SET bookmarked=:isBookmarked WHERE _id=:id")
    abstract fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE puzzles SET bookmarked = 0")
    abstract fun removeAllBookmarks()

    @Transaction
    open fun bulkUpdatePuzzles(saves: Set<PuzzleSave>) {
        for (save in saves) {
            updatePuzzle(
                save.id,
                save.game,
                save.notes,
                save.time,
                save.bookmarked,
                save.progress,
                save.completed,
                save.cheats
            )
        }
    }

    @Transaction
    open fun bulkGetPuzzles(ids: Set<Long>): List<PuzzleLoad> {
        val result = mutableListOf<PuzzleLoad>()
        ids.chunked(CHUNK_SIZE) {
            subset: List<Long> ->
            result.addAll(getPuzzles(subset.toSet()))
        }
        return result
    }

    companion object {
        private const val CHUNK_SIZE = 500
    }
}
