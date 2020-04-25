package com.jdamcd.sudoku.bookmark

import android.content.Context
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.base.Presenter
import com.jdamcd.sudoku.base.PresenterView
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class BookmarksPresenter @Inject constructor(
    private val repository: PuzzleRepository,
    private val intents: IntentFactory
) : Presenter<BookmarksPresenter.View>() {

    override fun start(view: BookmarksPresenter.View) {
        super.start(view)

        addSubscription(repository.getBookmarkedPuzzles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.showPuzzles(it) })

        addSubscription(view.onPuzzleClicked()
                .subscribe {
                    view.getContext()?.startActivity(intents.getPuzzle(it.id))
                })

        addSubscription(view.onRemoveAll()
                .flatMapCompletable {
                    repository.removeAllBookmarks()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()) }
                .subscribe())
    }

    internal interface View : PresenterView {
        fun showPuzzles(puzzles: List<Puzzle>)

        fun onPuzzleClicked(): Observable<Puzzle>
        fun onRemoveAll(): Observable<Any>
        fun getContext(): Context?
    }
}
