package com.jdamcd.sudoku.puzzle

import android.os.Bundle
import android.view.Menu
import com.jdamcd.sudoku.BuildConfig
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity
import com.jdamcd.sudoku.settings.user.Settings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleActivity : BaseActivity(), PuzzleFragment.PuzzleContract {

    @Inject lateinit var settings: Settings

    private var puzzleId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)
        setupActionBar(true)
        puzzleId = intent.getLongExtra(EXTRA_PUZZLE_ID, -1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (BuildConfig.DEBUG) {
            menuInflater.inflate(R.menu.activity_puzzle_debug, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun setPuzzleName(name: String) {
        supportActionBar?.title = name
    }

    override fun setTime(time: String) {
        if (!isFinishing) {
            supportActionBar?.subtitle = time
        }
    }

    override fun invalidateMenu() {
        invalidateOptionsMenu()
    }

    companion object {
        const val EXTRA_PUZZLE_ID = "puzzle_id"
    }
}
