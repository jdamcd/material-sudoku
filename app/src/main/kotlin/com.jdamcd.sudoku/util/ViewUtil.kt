package com.jdamcd.sudoku.util

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue

object ViewUtil {

    fun dpToPx(res: Resources, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), res.displayMetrics).toInt()
    }

    fun blendColours(first: Int, second: Int, ratio: Float): Int {
        val inverseRation = 1f - ratio
        val r = Color.red(second) * ratio + Color.red(first) * inverseRation
        val g = Color.green(second) * ratio + Color.green(first) * inverseRation
        val b = Color.blue(second) * ratio + Color.blue(first) * inverseRation
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
}
