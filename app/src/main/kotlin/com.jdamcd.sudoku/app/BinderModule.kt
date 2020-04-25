package com.jdamcd.sudoku.app

import com.jdamcd.sudoku.bookmark.BookmarksActivity
import com.jdamcd.sudoku.bookmark.BookmarksFragment
import com.jdamcd.sudoku.browse.PuzzleChoiceActivity
import com.jdamcd.sudoku.browse.PuzzleListFragment
import com.jdamcd.sudoku.browse.ResumePuzzleSheet
import com.jdamcd.sudoku.puzzle.PuzzleActivity
import com.jdamcd.sudoku.puzzle.PuzzleFragment
import com.jdamcd.sudoku.puzzle.dialog.PuzzleCompleteDialog
import com.jdamcd.sudoku.scoreboard.ScoreboardActivity
import com.jdamcd.sudoku.scoreboard.ScoreboardFragment
import com.jdamcd.sudoku.settings.SettingsActivity
import com.jdamcd.sudoku.settings.SettingsFragment
import com.jdamcd.sudoku.settings.howto.HowToPlayActivity
import com.jdamcd.sudoku.settings.howto.HowToPlayFragment
import com.jdamcd.sudoku.settings.license.LicensesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class BinderModule {

    @ContributesAndroidInjector
    internal abstract fun splashActivityInjector(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun puzzleChoiceActivityInjector(): PuzzleChoiceActivity

    @ContributesAndroidInjector
    internal abstract fun puzzleActivityInjector(): PuzzleActivity

    @ContributesAndroidInjector
    internal abstract fun scoreboardActivityInjector(): ScoreboardActivity

    @ContributesAndroidInjector
    internal abstract fun bookmarksActivityInjector(): BookmarksActivity

    @ContributesAndroidInjector
    internal abstract fun howToPlayActivityInjector(): HowToPlayActivity

    @ContributesAndroidInjector
    internal abstract fun settingsActivityInjector(): SettingsActivity

    @ContributesAndroidInjector
    internal abstract fun licensesActivityInjector(): LicensesActivity

    @ContributesAndroidInjector
    internal abstract fun puzzleListFragmentInjector(): PuzzleListFragment

    @ContributesAndroidInjector
    internal abstract fun puzzleFragmentInjector(): PuzzleFragment

    @ContributesAndroidInjector
    internal abstract fun scoreboardFragmentInjector(): ScoreboardFragment

    @ContributesAndroidInjector
    internal abstract fun bookmarkListFragmentInjector(): BookmarksFragment

    @ContributesAndroidInjector
    internal abstract fun settingsFragmentInjector(): SettingsFragment

    @ContributesAndroidInjector
    internal abstract fun howToPlayFragmentInjector(): HowToPlayFragment

    @ContributesAndroidInjector
    internal abstract fun puzzleCompleteDialogInjector(): PuzzleCompleteDialog

    @ContributesAndroidInjector
    internal abstract fun resumePuzzleSheetInjector(): ResumePuzzleSheet
}
