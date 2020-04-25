package com.jdamcd.sudoku.browse.indicator

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

interface PageIndicator : OnPageChangeListener {

    fun setViewPager(view: ViewPager)

    fun setViewPager(view: ViewPager, initialPosition: Int)

    fun setCurrentItem(item: Int)

    fun setOnPageChangeListener(listener: OnPageChangeListener)

    fun notifyDataSetChanged()
}
