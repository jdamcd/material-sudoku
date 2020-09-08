package com.jdamcd.sudoku.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class Strings @Inject constructor(@ApplicationContext private val context: Context) {

    fun puzzleName(@StringRes levelRes: Int, number: Int) = context.getString(levelRes) + " #" + number

    companion object {

        const val EMPTY = ""

        fun formatTime(millis: Long): String {
            var seconds = (millis / 1000).toInt()
            var minutes = seconds / 60
            val hours = minutes / 60
            minutes %= 60
            seconds %= 60

            return if (hours == 0) {
                String.format(Locale.US, "%d:%02d", minutes, seconds)
            } else {
                String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }
}
