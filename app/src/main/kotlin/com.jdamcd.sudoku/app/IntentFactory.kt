package com.jdamcd.sudoku.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jdamcd.sudoku.bookmark.BookmarksActivity
import com.jdamcd.sudoku.browse.PuzzleChoiceActivity
import com.jdamcd.sudoku.puzzle.PuzzleActivity
import com.jdamcd.sudoku.scoreboard.ScoreboardActivity
import com.jdamcd.sudoku.settings.SettingsActivity
import com.jdamcd.sudoku.settings.license.LicensesActivity
import com.jdamcd.sudoku.shortcut.ShortcutController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntentFactory @Inject constructor(@ApplicationContext private val context: Context) {

    fun getPuzzleChoice(): Intent {
        val intent = Intent(context, PuzzleChoiceActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    fun getPuzzleChoice(resumePromptId: Long): Intent {
        val intent = getPuzzleChoice()
        intent.putExtra(EXTRA_RESUME_ID, resumePromptId)
        return intent
    }

    fun getPlayRandom(): Intent {
        val intent = getPuzzleChoice()
        intent.putExtra(EXTRA_SHORTCUT, ShortcutController.ID_RANDOM)
        return intent
    }

    fun getResumeSplash(): Intent {
        val intent = Intent(context, SplashActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(EXTRA_SHORTCUT, ShortcutController.ID_RESUME)
        return intent
    }

    fun getResumePuzzle(resumeId: Long): Intent {
        val intent = getPuzzleChoice()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(EXTRA_SHORTCUT, ShortcutController.ID_RESUME)
        intent.putExtra(EXTRA_RESUME_ID, resumeId)
        return intent
    }

    fun getScoreboard(): Intent {
        return Intent(context, ScoreboardActivity::class.java)
    }

    fun getPuzzle(id: Long): Intent {
        val intent = Intent(context, PuzzleActivity::class.java)
        intent.putExtra(PuzzleActivity.EXTRA_PUZZLE_ID, id)
        return intent
    }

    fun getBookmarks(): Intent {
        return Intent(context, BookmarksActivity::class.java)
    }

    fun getSettings(): Intent {
        val intent = Intent(context, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    fun getLicenses(): Intent {
        return Intent(context, LicensesActivity::class.java)
    }

    fun getRateApp(): Intent {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        return Intent(Intent.ACTION_VIEW, uri)
    }

    companion object {
        const val EXTRA_RESUME_ID = "extra_resume_id"
        const val EXTRA_SHORTCUT = "extra_shortcut"

        fun isRandomShortcut(intent: Intent) = intent.hasExtra(EXTRA_SHORTCUT) &&
            intent.getStringExtra(EXTRA_SHORTCUT) == ShortcutController.ID_RANDOM

        fun isResumeShortcut(intent: Intent) = intent.hasExtra(EXTRA_SHORTCUT) &&
            intent.getStringExtra(EXTRA_SHORTCUT) == ShortcutController.ID_RESUME
    }
}
