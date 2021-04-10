package com.jdamcd.sudoku.browse

import android.content.Context
import com.jdamcd.sudoku.app.EventBus
import com.jdamcd.sudoku.app.HideCompletedEvent
import com.jdamcd.sudoku.app.IntentFactory
import com.jdamcd.sudoku.base.Presenter
import com.jdamcd.sudoku.base.PresenterView
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import com.jdamcd.sudoku.settings.user.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class PuzzleListPresenter @Inject constructor(
    private val repository: PuzzleRepository,
    private val eventBus: EventBus,
    private val settings: Settings,
    private val intents: IntentFactory
) : Presenter<PuzzleListPresenter.View>() {

    private var listDisposable = Disposables.empty()

    override fun start(view: View) {
        super.start(view)

        setListSubscription(view, settings.hideCompleted)

        addSubscription(
            eventBus.listen(HideCompletedEvent::class.java)
                .map { it == HideCompletedEvent.HIDE }
                .subscribe { setListSubscription(view, it) }
        )

        addSubscription(
            view.onPuzzleClicked()
                .subscribe { view.getContext()?.startActivity(intents.getPuzzle(it.id)) }
        )
    }

    private fun setListSubscription(view: View, hideCompleted: Boolean) {
        removeSubscription(listDisposable)
        listDisposable = repository.getPuzzles(view.getLevel(), hideCompleted)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.showPuzzles(it) }
        addSubscription(listDisposable)
    }

    internal interface View : PresenterView {
        fun showPuzzles(puzzles: List<Puzzle>)

        fun onPuzzleClicked(): Observable<Puzzle>
        fun getLevel(): Level
        fun getContext(): Context?
    }
}
