package com.jdamcd.sudoku.scoreboard

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Keep
import com.jdamcd.sudoku.R
import java.util.Arrays

class LevelGraphView(context: Context, attrs: AttributeSet) : View(context, attrs), AnimatorUpdateListener {

    private var counts: IntArray = IntArray(NUM_BARS)
    private val barWidths: IntArray = IntArray(NUM_BARS)

    private var graphWidth: Int = 0
    private var barHeight: Int = 0

    private val barColours: IntArray
    private val barPaint: Paint

    private var pendingAnimation: Boolean = false

    init {
        Arrays.fill(barWidths, MIN_WIDTH)
        barPaint = Paint()
        barColours = resources.getIntArray(R.array.level_colours)
    }

    fun setCounts(counts: IntArray) {
        this.counts = counts
        if (graphWidth == 0) {
            pendingAnimation = true
        } else {
            startBarAnimations()
        }
    }

    private fun startBarAnimations() {
        val animator = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofInt("easyWidth", MIN_WIDTH, getBarWidth(0)),
                PropertyValuesHolder.ofInt("normalWidth", MIN_WIDTH, getBarWidth(1)),
                PropertyValuesHolder.ofInt("hardWidth", MIN_WIDTH, getBarWidth(2)),
                PropertyValuesHolder.ofInt("extremeWidth", MIN_WIDTH, getBarWidth(3)))

        animator.startDelay = ANIM_DELAY.toLong()
        animator.duration = ANIM_TIME.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener(this)
        animator.start()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        this.graphWidth = width
        barHeight = height / NUM_BARS
        if (pendingAnimation) {
            pendingAnimation = false
            startBarAnimations()
        }
    }

    public override fun onDraw(canvas: Canvas) {
        for (i in counts.indices) {
            drawBar(canvas, i)
        }
    }

    private fun drawBar(canvas: Canvas, i: Int) {
        barPaint.color = barColours[i % barColours.size]
        canvas.drawRect(0f, (i * barHeight).toFloat(), barWidths[i].toFloat(), ((i + 1) * barHeight).toFloat(), barPaint)
    }

    private fun getBarWidth(i: Int): Int {
        val max = counts.max() ?: 0
        if (max > 0) {
            val barWidth = (counts[i].toFloat() / max.toFloat() * width).toInt()
            return if (barWidth > MIN_WIDTH) barWidth else MIN_WIDTH
        }
        return MIN_WIDTH
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        invalidate()
    }

    /*
     * Setters required for ObjectAnimator to update widths
     */

    @Keep
    fun setEasyWidth(width: Int) {
        barWidths[0] = width
    }

    @Keep
    fun setNormalWidth(width: Int) {
        barWidths[1] = width
    }

    @Keep
    fun setHardWidth(width: Int) {
        barWidths[2] = width
    }

    @Keep
    fun setExtremeWidth(width: Int) {
        barWidths[3] = width
    }

    companion object {
        private const val NUM_BARS = 4
        private const val MIN_WIDTH = 10
        private const val ANIM_DELAY = 500
        private const val ANIM_TIME = 500
    }
}
