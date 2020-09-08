package com.jdamcd.sudoku

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.jdamcd.sudoku.repository.database.AssetDb
import com.jdamcd.sudoku.repository.database.PuzzleDao
import com.jdamcd.sudoku.repository.database.PuzzleDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before

open class DatabaseTest {

    private lateinit var db: PuzzleDatabase
    protected lateinit var dao: PuzzleDao

    @Before
    open fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.databaseBuilder(context, PuzzleDatabase::class.java, AssetDb.NAME)
            .createFromAsset(AssetDb.PATH)
            .addMigrations(PuzzleDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
        dao = db.puzzleDao()
    }

    @After
    fun tearDown() {
        db.close()
        assertThat(
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
                .getDatabasePath(AssetDb.NAME)
                .delete()
        ).isTrue()
    }
}
