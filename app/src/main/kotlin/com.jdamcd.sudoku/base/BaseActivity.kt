package com.jdamcd.sudoku.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.jdamcd.sudoku.R

abstract class BaseActivity : AppCompatActivity() {

    protected fun setupActionBar(showUp: Boolean): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
        return toolbar
    }
}
