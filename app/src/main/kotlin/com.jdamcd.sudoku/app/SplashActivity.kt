package com.jdamcd.sudoku.app

import android.os.Bundle
import com.jdamcd.sudoku.base.BaseActivity
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.shortcut.ShortcutController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Inject lateinit var intents: IntentFactory
    @Inject lateinit var shortcuts: ShortcutController
    @Inject lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            IntentFactory.isRandomShortcut(intent) -> playRandom()
            IntentFactory.isResumeShortcut(intent) -> resumePuzzle()
            settings.resumePrompt -> startActivity(intents.getPuzzleChoice(settings.lastPlayed))
            else -> startActivity(intents.getPuzzleChoice())
        }
        finish()
    }

    private fun resumePuzzle() {
        shortcuts.reportResumeUsed()
        startActivity(intents.getResumePuzzle(settings.lastPlayed))
    }

    private fun playRandom() {
        shortcuts.reportRandomUsed()
        startActivity(intents.getPlayRandom())
    }
}
