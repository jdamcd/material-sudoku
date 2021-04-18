package com.jdamcd.sudoku.browse.indicator

import androidx.viewpager2.widget.ViewPager2

interface PageIndicator {

    fun setViewPager(view: ViewPager2)

    fun setViewPager(view: ViewPager2, initialPosition: Int)

    fun setCurrentItem(item: Int)

    fun setOnPageChangeCallback(listener: ViewPager2.OnPageChangeCallback)

    fun notifyDataSetChanged()
}
