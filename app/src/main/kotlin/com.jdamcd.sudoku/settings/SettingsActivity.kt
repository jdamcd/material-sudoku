package com.jdamcd.sudoku.settings

import android.os.Bundle
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        setupActionBar(true)
    }
}
