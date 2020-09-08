package com.jdamcd.sudoku.scoreboard

import android.os.Bundle
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        setupActionBar(true)
    }
}
