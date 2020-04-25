package com.jdamcd.sudoku.browse

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jdamcd.sudoku.R

internal class RandomFabView : ViewPager.OnPageChangeListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var fabColours: IntArray

    fun setup(fab: FloatingActionButton) {
        this.fab = fab
        fabColours = fab.resources.getIntArray(R.array.level_colours)
        setBackground(colourAtPosition(1))
    }

    private fun colourAtPosition(position: Int): Int {
        return fabColours[position % fabColours.size]
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        animateBackgroundChange(position)
    }

    private fun animateBackgroundChange(position: Int) {
        val startColor = fab.backgroundTintList?.defaultColor
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), startColor, colourAtPosition(position))
        colorAnimation.addUpdateListener { animator -> setBackground(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    private fun setBackground(color: Int) {
        fab.backgroundTintList = ColorStateList.valueOf(color)
    }
}
