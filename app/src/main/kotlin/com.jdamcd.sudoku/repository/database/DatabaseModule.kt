package com.jdamcd.sudoku.repository.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): PuzzleDatabase {
        return Room.databaseBuilder(context, PuzzleDatabase::class.java, AssetDb.NAME)
            .createFromAsset(AssetDb.PATH)
            .addMigrations(PuzzleDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePuzzleDao(db: PuzzleDatabase): PuzzleDao {
        return db.puzzleDao()
    }
}
