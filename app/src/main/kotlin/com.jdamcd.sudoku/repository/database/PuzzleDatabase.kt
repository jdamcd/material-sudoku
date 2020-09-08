package com.jdamcd.sudoku.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [(PuzzleLoad::class)], version = 2)
internal abstract class PuzzleDatabase : RoomDatabase() {

    abstract fun puzzleDao(): PuzzleDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val table = "puzzles"
                val copyTable = "copy"

                // V2: Create puzzle table with extra schema constraints for Room
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `$copyTable` (" +
                        "`_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "`level` TEXT NOT NULL, " +
                        "`number` INTEGER NOT NULL, " +
                        "`givens` TEXT NOT NULL, " +
                        "`solution` TEXT NOT NULL, " +
                        "`game` TEXT, " +
                        "`notes` TEXT, " +
                        "`time` INTEGER, " +
                        "`bookmarked` INTEGER, " +
                        "`progress` INTEGER, " +
                        "`completed` INTEGER, " +
                        "`cheats` INTEGER)"
                )

                database.execSQL(
                    "INSERT INTO $copyTable (_id, level, number, givens, solution, game, notes, time, bookmarked, progress, completed, cheats) " +
                        "SELECT _id, level, number, givens, solution, game, notes, time, bookmarked, progress, completed, cheats " +
                        "FROM $table"
                )

                database.execSQL("DROP TABLE $table")
                database.execSQL("ALTER TABLE $copyTable RENAME TO $table")
            }
        }
    }
}
