package com.jdamcd.sudoku.scoreboard

import com.jdamcd.sudoku.base.Presenter
import com.jdamcd.sudoku.base.PresenterView
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class ScoreboardPresenter @Inject constructor(private val repository: PuzzleRepository) : Presenter<ScoreboardPresenter.View>() {

    override fun start(view: View) {
        super.start(view)

        addSubscription(
            repository.getCompletedPuzzles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { completed -> view.showSummary(completed.size, extractCounts(completed)) }
        )

        addSubscription(
            repository.getCompletedPuzzles()
                .toObservable()
                .flatMap { list ->
                    Observable.fromIterable(list)
                        .groupBy { it.level }
                        .filter { it.key != Level.SPECIAL }
                }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    it.toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::showLevelStats)
                }
        )
    }

    private fun extractCounts(puzzles: List<Puzzle>): IntArray {
        val counts = IntArray(4)
        for (puzzle in puzzles) when (puzzle.level) {
            Level.EASY -> counts[0]++
            Level.MEDIUM -> counts[1]++
            Level.HARD -> counts[2]++
            Level.EXTREME -> counts[3]++
            else -> {}
        }
        return counts
    }

    internal interface View : PresenterView {
        fun showSummary(count: Int, countsByLevel: IntArray)
        fun showLevelStats(completed: List<Puzzle>)
    }
}
