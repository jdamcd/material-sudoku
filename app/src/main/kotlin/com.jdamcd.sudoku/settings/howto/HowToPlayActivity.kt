package com.jdamcd.sudoku.settings.howto

import android.os.Bundle
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowToPlayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_play)
        setupActionBar(true)
    }
}
