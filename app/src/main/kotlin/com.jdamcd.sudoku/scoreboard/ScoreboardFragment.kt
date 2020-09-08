package com.jdamcd.sudoku.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseFragment
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Level.EASY
import com.jdamcd.sudoku.repository.Level.EXTREME
import com.jdamcd.sudoku.repository.Level.HARD
import com.jdamcd.sudoku.repository.Level.MEDIUM
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.util.Strings
import kotlinx.android.synthetic.main.fragment_scoreboard.*
import kotlinx.android.synthetic.main.layout_scorecard_summary.*
import javax.inject.Inject

class ScoreboardFragment : BaseFragment(), ScoreboardPresenter.View {

    @Inject internal lateinit var presenter: ScoreboardPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scoreboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.start(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.stop()
    }

    override fun showSummary(count: Int, countsByLevel: IntArray) {
        if (count == 0) {
            summary_empty.visibility = View.VISIBLE
            summary_graph.visibility = View.GONE
            summary_total.visibility = View.GONE
        } else {
            summary_empty.visibility = View.GONE
            summary_graph.visibility = View.VISIBLE
            summary_total.visibility = View.VISIBLE
        }
        summary_total.text = count.toString()
        summary_graph.setCounts(countsByLevel)
    }

    override fun showLevelStats(completed: List<Puzzle>) {
        val best = BestTime()
        var totalTime: Long = 0

        for (puzzle in completed) {
            if (best.worseThan(puzzle.time, puzzle.numberOfCheats)) {
                best[puzzle.time, puzzle.numberOfCheats] = puzzle.number.toString()
            }
            totalTime += puzzle.time
        }

        val averageTime = totalTime / completed.size
        displayCard(completed[0].level, completed.size, best, averageTime)
    }

    private fun displayCard(level: Level, puzzlesCompleted: Int, best: BestTime, averageTime: Long) {
        val cardView = getCardView(level)
        val title = cardView.findViewById<TextView>(R.id.level)
        title.text = getString(level.nameId)
        title.setTextColor(ContextCompat.getColor(cardView.context, level.colourId))

        setText(cardView, R.id.completed_field, puzzlesCompleted.toString())
        setText(cardView, R.id.best_field, Strings.formatTime(best.time))
        setText(cardView, R.id.best_details_field, formatBestTimeDetails(best))
        setText(cardView, R.id.average_field, Strings.formatTime(averageTime))
        cardView.visibility = View.VISIBLE
    }

    private fun formatBestTimeDetails(best: BestTime): String {
        var puzzle = "#" + best.puzzle
        if (best.hasCheats()) {
            puzzle = puzzle + " - " + resources.getQuantityString(R.plurals.scorecard_cheats, best.cheats, best.cheats)
        }
        return puzzle
    }

    private fun getCardView(level: Level): View {
        return when (level) {
            EASY -> scorecard_easy
            MEDIUM -> scorecard_medium
            HARD -> scorecard_hard
            EXTREME -> scorecard_extreme
            else -> throw IllegalStateException("Unexpected level")
        }
    }

    private fun setText(root: View, textViewId: Int, text: String) {
        root.findViewById<TextView>(textViewId).text = text
    }

    private inner class BestTime {
        var time = java.lang.Long.MAX_VALUE
        var cheats = Integer.MAX_VALUE
        lateinit var puzzle: String

        internal operator fun set(time: Long, cheats: Int, puzzle: String) {
            this.time = time
            this.cheats = cheats
            this.puzzle = puzzle
        }

        internal fun hasCheats(): Boolean {
            return cheats > 0
        }

        internal fun worseThan(time: Long, cheats: Int): Boolean {
            return cheats < this.cheats || cheats == this.cheats && time < this.time
        }
    }
}
