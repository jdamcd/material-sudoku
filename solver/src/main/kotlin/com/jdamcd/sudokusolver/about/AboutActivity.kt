package com.jdamcd.sudokusolver.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jdamcd.sudokusolver.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_about)
    }
}
