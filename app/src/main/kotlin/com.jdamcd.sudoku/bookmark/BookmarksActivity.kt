package com.jdamcd.sudoku.bookmark

import android.os.Bundle
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        setupActionBar(true)
    }
}
