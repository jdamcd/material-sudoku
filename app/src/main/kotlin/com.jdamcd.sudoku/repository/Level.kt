package com.jdamcd.sudoku.repository

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jdamcd.sudoku.R

enum class Level(
    val id: String,
    @StringRes val nameId: Int,
    @ColorRes val colourId: Int
) {

    EASY("easy", R.string.level_easy, R.color.easy),
    MEDIUM("medium", R.string.level_medium, R.color.normal),
    HARD("hard", R.string.level_hard, R.color.hard),
    EXTREME("extreme", R.string.level_extreme, R.color.extreme),
    SPECIAL("special", R.string.level_special, R.color.normal);

    companion object {

        fun fromId(id: String): Level {
            return Level.values().firstOrNull { it.id == id } ?: SPECIAL
        }

        @DrawableRes
        fun progressDrawable(level: Level): Int {
            return when (level) {
                EASY -> R.drawable.progress_easy
                MEDIUM -> R.drawable.progress_normal
                HARD -> R.drawable.progress_hard
                EXTREME -> R.drawable.progress_extreme
                SPECIAL -> R.drawable.progress_normal
            }
        }
    }
}
