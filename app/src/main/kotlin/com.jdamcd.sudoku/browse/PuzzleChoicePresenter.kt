package com.jdamcd.sudoku.browse

import android.content.Context
import com.jdamcd.sudoku.BuildConfig
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.base.Presenter
import com.jdamcd.sudoku.base.PresenterView
import com.jdamcd.sudoku.eventbus.EventBus
import com.jdamcd.sudoku.eventbus.event.HideCompleted
import com.jdamcd.sudoku.eventbus.event.SavesSync
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import com.jdamcd.sudoku.settings.user.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

internal class PuzzleChoicePresenter @Inject constructor(
    private val repository: PuzzleRepository,
    private val eventBus: EventBus,
    private val settings: Settings,
    private val intents: IntentFactory
) : Presenter<PuzzleChoicePresenter.View>() {

    override fun start(view: View) {
        super.start(view)

        if (BuildConfig.GOOGLE && !settings.ratingPromptShown) {
            setupRatingPrompt(view)
        }

        addSubscription(
            eventBus.listen(SavesSync::class.java)
                .subscribe {
                    view.showSyncStatus(it == SavesSync.SYNCING)
                }
        )

        addSubscription(
            view.onToggleCompleted()
                .subscribe {
                    settings.hideCompleted = it
                    eventBus.publish(if (it) HideCompleted.HIDE else HideCompleted.SHOW)
                }
        )

        addSubscription(
            view.onFabClick()
                .subscribe {
                    playRandomPuzzle(view, it)
                }
        )
    }

    fun playRandomPuzzle(view: View, level: Level) {
        addSubscription(
            repository.getRandomUnplayedPuzzleId(level)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { id -> view.getContext().startActivity(intents.getPuzzle(id)) },
                    { view.showRandomError() }
                )
        )
    }

    private fun setupRatingPrompt(view: View) {
        addSubscription(
            repository.countCompleted()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { count ->
                    if (count >= Settings.RATING_THRESHOLD && !settings.ratingPromptShown) {
                        view.showRatingPrompt()
                    }
                }
        )
    }

    fun loadInProgressPuzzle(resumeId: Long, prompt: Boolean) {
        addSubscription(
            repository.getPuzzle(resumeId)
                .filter { puzzle ->
                    !puzzle.isCompleted &&
                        puzzle.game.getNumberOfCorrectAnswers() > 0
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { puzzle ->
                    if (prompt) view?.showResumePrompt(puzzle)
                    else view?.openPuzzle(puzzle)
                }
        )
    }

    internal interface View : PresenterView {
        fun showRatingPrompt()
        fun showRandomError()
        fun showSyncStatus(isSyncing: Boolean)
        fun showResumePrompt(puzzle: Puzzle)
        fun openPuzzle(puzzle: Puzzle)

        fun onToggleCompleted(): Observable<Boolean>
        fun onFabClick(): Observable<Level>
        fun getContext(): Context
    }
}
