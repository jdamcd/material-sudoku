package com.jdamcd.sudoku.browse

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jdamcd.sudoku.repository.Level

internal class PuzzlePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = levels.size

    override fun createFragment(position: Int) = PuzzleListFragment.create(levels[position])

    companion object {
        val levels = arrayOf(Level.EASY, Level.MEDIUM, Level.HARD, Level.EXTREME)
    }
}
