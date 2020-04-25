package com.jdamcd.sudoku.settings.license

import android.os.Bundle
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity

class LicensesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        setupActionBar(true)
    }
}
