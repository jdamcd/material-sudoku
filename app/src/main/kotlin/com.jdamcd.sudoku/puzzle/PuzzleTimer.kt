package com.jdamcd.sudoku.puzzle

import android.os.Handler
import com.jdamcd.sudoku.util.Strings

internal class PuzzleTimer(private val callback: UpdateCallback) {

    private val timeHandler = Handler()
    private var startTime = 0L
    private var timeOffset = 0L
    private var currentText: String? = null

    private val updateTimeTask = object : Runnable {
        override fun run() {
            val updatedText = this@PuzzleTimer.toString()
            if (updatedText != currentText) {
                callback.update(updatedText)
                currentText = updatedText
            }
            timeHandler.removeCallbacks(this)
            timeHandler.postDelayed(this, UPDATE_INTERVAL)
        }
    }

    interface UpdateCallback {
        fun update(time: String)
    }

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun restart() {
        timeOffset = 0L
        startTime = System.currentTimeMillis()
    }

    fun setStartAt(time: Long, update: Boolean) {
        timeOffset = time
        callback.update(if (update) toString() else Strings.EMPTY)
    }

    fun getStartTime() = Strings.formatTime(timeOffset)

    fun getTime(): Long = System.currentTimeMillis() - startTime + timeOffset

    fun startUpdates() {
        timeHandler.removeCallbacks(updateTimeTask)
        timeHandler.post(updateTimeTask)
    }

    fun pause() {
        timeOffset = getTime()
    }

    fun stopUpdates() {
        timeHandler.removeCallbacks(updateTimeTask)
    }

    override fun toString() = Strings.formatTime(getTime())

    companion object {
        private const val UPDATE_INTERVAL = 25L
    }
}
