package com.jdamcd.sudoku.browse

import android.content.res.Resources
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jdamcd.sudoku.repository.Level

internal class PuzzlePagerAdapter(fm: FragmentManager, private val resources: Resources) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = PuzzleListFragment.create(levels[position])

    override fun getPageTitle(position: Int): String = resources.getString(levels[position].nameId)

    override fun getCount() = levels.size

    companion object {
        val levels = arrayOf(Level.EASY, Level.MEDIUM, Level.HARD, Level.EXTREME)
    }
}
