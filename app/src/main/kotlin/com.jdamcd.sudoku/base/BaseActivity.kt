package com.jdamcd.sudoku.base

import androidx.appcompat.widget.Toolbar
import com.jdamcd.sudoku.R
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

    protected fun setupActionBar(showUp: Boolean): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
        return toolbar
    }
}
