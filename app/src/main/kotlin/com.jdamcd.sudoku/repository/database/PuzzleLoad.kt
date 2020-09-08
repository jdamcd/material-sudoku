package com.jdamcd.sudoku.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.util.Format
import com.jdamcd.sudoku.util.Strings

@Entity(tableName = "puzzles")
data class PuzzleLoad(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Long,
    @ColumnInfo(name = "level") val level: String,
    @ColumnInfo(name = "number") val number: Int,
    @ColumnInfo(name = "givens") val givens: String,
    @ColumnInfo(name = "solution") val solution: String,
    @ColumnInfo(name = "game") val game: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "time") val time: Long?,
    @ColumnInfo(name = "bookmarked") val bookmarked: Boolean?,
    @ColumnInfo(name = "progress") val progress: Int?,
    @ColumnInfo(name = "completed") val completed: Boolean?,
    @ColumnInfo(name = "cheats") val cheats: Int?
) {

    fun toPuzzle(strings: Strings): Puzzle {
        val mappedLevel = Level.fromId(level)
        val sudoku = Sudoku(givens)
        sudoku.solution = Format.gridFromString(solution)
        val game = game(sudoku, game, notes)
        game.numberOfCheats = cheats ?: 0
        return Puzzle(
            id,
            mappedLevel,
            number,
            strings.puzzleName(mappedLevel.nameId, number),
            sudoku,
            game,
            solution,
            time ?: 0,
            bookmarked ?: false,
            completed ?: false,
            cheats ?: 0
        )
    }

    private fun game(sudoku: Sudoku, game: String?, notes: String?): Game {
        return if (!game.isNullOrEmpty() && !notes.isNullOrEmpty()) {
            Game(sudoku, Format.gridFromString(game), Format.deserialiseNotes(notes))
        } else if (!game.isNullOrEmpty()) {
            Game(sudoku, Format.gridFromString(game))
        } else {
            Game(sudoku)
        }
    }
}
