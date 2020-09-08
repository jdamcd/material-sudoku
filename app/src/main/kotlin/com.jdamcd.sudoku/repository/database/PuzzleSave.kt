package com.jdamcd.sudoku.repository.database

data class PuzzleSave(
    val id: Long,
    val game: String?,
    val notes: String?,
    val time: Long,
    val bookmarked: Boolean,
    val progress: Int,
    val completed: Boolean,
    val cheats: Int
) {

    companion object {

        fun forRestart(id: Long) = PuzzleSave(
            id,
            null,
            null,
            0,
            true,
            0,
            false,
            0
        )

        fun forCompleted(id: Long, game: String, time: Long, cheats: Int) = PuzzleSave(
            id,
            game,
            null,
            time,
            false,
            100,
            true,
            cheats
        )
    }
}
