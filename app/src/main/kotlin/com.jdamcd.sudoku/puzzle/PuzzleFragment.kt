package com.jdamcd.sudoku.puzzle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.eventbus.EventBus
import com.jdamcd.sudoku.eventbus.event.GameResult
import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Game
import com.jdamcd.sudoku.puzzle.PuzzleTimer.UpdateCallback
import com.jdamcd.sudoku.puzzle.dialog.ConfirmRestartDialog
import com.jdamcd.sudoku.puzzle.dialog.PuzzleCompleteDialog
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import com.jdamcd.sudoku.repository.database.PuzzleSave
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.shortcut.ShortcutController
import com.jdamcd.sudoku.util.Format
import com.jdamcd.sudoku.util.snackbar
import com.jdamcd.sudoku.view.GamePuzzleView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleFragment : Fragment(), ConfirmRestartDialog.RestartContract {

    @Inject lateinit var repository: PuzzleRepository
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var settings: Settings
    @Inject lateinit var shortcuts: ShortcutController

    private lateinit var boardView: GamePuzzleView
    private lateinit var keypad: PuzzleKeypad

    private var puzzleId: Long = 0
    private lateinit var level: Level
    private lateinit var timer: PuzzleTimer
    private lateinit var game: Game
    private var isBookmarked: Boolean = false
    private var isCompleted: Boolean = false

    private var disposable = Disposables.empty()

    private val hostActivity: PuzzleContract
        get() = activity as PuzzleContract

    internal interface PuzzleContract {
        fun setPuzzleName(name: String)
        fun setTime(time: String)
        fun invalidateMenu()
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        puzzleId = requireActivity().intent.getLongExtra(PuzzleActivity.EXTRA_PUZZLE_ID, -1)
        timer = PuzzleTimer(
            object : UpdateCallback {
                override fun update(time: String) {
                    hostActivity.setTime(time)
                }
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_puzzle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        disposable = repository.getPuzzle(puzzleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data -> setupPuzzle(data) }
    }

    private fun findViews(root: View) {
        keypad = PuzzleKeypad(
            root,
            this::setCursorCell,
            this::setCursorNote,
            onClearValue = {
                clearCell(boardView.cursorPosition)
                onCellChanged()
            },
            onToggleNotes = {
                invalidateUIState()
            }
        )
        boardView = root.findViewById(R.id.puzzle_board)
    }

    private fun setupPuzzle(data: Puzzle) {
        if (!::game.isInitialized) {
            game = data.game
        }
        boardView.setGame(game)
        boardView.setShowMistakes(settings.showErrors)

        hostActivity.setPuzzleName(data.title)
        level = data.level
        timer.setStartAt(data.time, settings.timerEnabled)

        isCompleted = data.isCompleted
        isBookmarked = data.isBookmarked || data.time == 0L // First open
        hostActivity.invalidateMenu()

        if (isCompleted) {
            timer.stopUpdates()
            hostActivity.setTime(timer.getStartTime())
            setViewsEnabled(false)
        } else {
            invalidateUIState()
        }

        boardView.setOnCellSelectedListener(
            object : GamePuzzleView.OnCellSelectedListener {
                override fun onCellSelected(position: CellPosition) {
                    invalidateUIState()
                }
            }
        )
        keypad.setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        timer.start()
        if (settings.timerEnabled && !isCompleted) {
            timer.startUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        timer.stopUpdates()
        if (::game.isInitialized && !isCompleted) {
            savePausedState()
        }
        timer.pause()
    }

    private fun saveBookmarkState() {
        repository.setBookmarked(puzzleId, isBookmarked)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun savePausedState() {
        repository.save(
            PuzzleSave(
                puzzleId,
                Format.stringFromGrid(game.answers),
                Format.serialiseNotes(game.notes),
                timer.getTime(),
                isBookmarked,
                game.getPercentageCorrect(),
                isCompleted,
                game.numberOfCheats
            )
        )
            .subscribeOn(Schedulers.io())
            .subscribe()
        setupResumePrompts()
    }

    private fun setupResumePrompts() {
        if (!isCompleted && game.getNumberOfCorrectAnswers() > 0) {
            settings.lastPlayed = puzzleId
            settings.resumePrompt = true
            shortcuts.enableResume()
        }
    }

    private fun saveCompletedState() {
        repository.save(
            PuzzleSave.forCompleted(
                puzzleId,
                Format.stringFromGrid(game.answers),
                timer.getTime(),
                game.numberOfCheats
            )
        )
            .subscribeOn(Schedulers.io())
            .subscribe()
        clearResumePrompts()
    }

    private fun clearResumePrompts() {
        settings.resumePrompt = false
        shortcuts.disableResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!::game.isInitialized) {
            return super.onCreateOptionsMenu(menu, inflater)
        }
        if (settings.cheatsEnabled) {
            inflater.inflate(R.menu.fragment_puzzle_cheat, menu)
            menu.findItem(R.id.action_cheat_cell).isEnabled = !isCompleted && isEmptySelected()
            menu.findItem(R.id.action_cheat_random_cell).isEnabled = !isCompleted
        } else {
            inflater.inflate(R.menu.fragment_puzzle, menu)
        }
        updateBookmarkItem(menu)
        updateClearNotesItem(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateBookmarkItem(menu: Menu) {
        val bookmark = menu.findItem(R.id.action_bookmark)
        bookmark.setTitle(if (isBookmarked) R.string.action_remove_bookmark else R.string.action_bookmark)
        bookmark.setIcon(if (isBookmarked) R.drawable.ic_action_bookmark_on else R.drawable.ic_action_bookmark_off)
    }

    private fun updateClearNotesItem(menu: Menu) {
        val clearNotes = menu.findItem(R.id.action_clear_notes)
        val hasNotes = game.hasNotes()
        clearNotes.isVisible = hasNotes && !isCompleted
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bookmark -> {
                isBookmarked = !isBookmarked
                hostActivity.invalidateMenu()
                saveBookmarkState()
                return true
            }
            R.id.action_cheat_cell -> {
                cheatCell()
                return true
            }
            R.id.action_cheat_random_cell -> {
                cheatRandomCell()
                return true
            }
            R.id.action_clear_notes -> {
                clearAllNotes()
                return true
            }
            R.id.action_restart -> {
                restartGame()
                return true
            }
            R.id.action_undo -> {
                tryUndo()
                return true
            }
            R.id.action_debug_complete -> {
                completePuzzleDebug()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun completePuzzleDebug() {
        game.cheatAll()
        onCellChanged()
    }

    private fun restartGame() {
        if (game.isStarted()) {
            showRestartConfirmation()
        } else {
            onRestart()
        }
    }

    private fun clearAllNotes() {
        game.clearNotes()
        onCellChanged()
        boardView.invalidate()
        hostActivity.invalidateMenu()
    }

    private fun tryUndo() {
        when {
            isCompleted -> boardView.snackbar(R.string.toast_puzzle_completed)
            game.canUndo() -> undo()
            else -> boardView.snackbar(R.string.toast_no_move_history)
        }
    }

    private fun undo() {
        val undoCell = game.undo()
        boardView.setCursor(undoCell.row, undoCell.col)
        onCellChanged()
    }

    private fun setCursorCell(value: Int) {
        val cursor = boardView.cursorPosition
        if (cursor.isSet()) {
            if (keypad.isNotesMode()) {
                game.toggleNote(cursor.row, cursor.col, value)
            } else {
                answerCell(cursor, value)
            }
            onCellChanged()
        } else {
            boardView.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun setCursorNote(value: Int) {
        val cursor = boardView.cursorPosition
        if (cursor.isSet()) {
            game.toggleNote(cursor.row, cursor.col, value)
            onCellChanged()
        } else {
            boardView.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun answerCell(cursor: CellPosition, value: Int) {
        if (game.getAnswer(cursor.row, cursor.col) == value) {
            game.setAnswer(cursor.row, cursor.col, 0)
        } else {
            game.setAnswer(cursor.row, cursor.col, value)
        }
    }

    private fun clearCell(cursor: CellPosition) {
        if (cursor.isSet()) {
            game.clear(cursor.row, cursor.col)
        } else {
            boardView.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun onCellChanged() {
        boardView.invalidate()
        invalidateUIState()
        if (game.isCompleted()) {
            setCompleted()
        }
    }

    private fun invalidateUIState() {
        hostActivity.invalidateMenu()
        val cursor = boardView.cursorPosition
        val isGivenSelected = isGivenSelected(cursor)
        keypad.setNumbersEnabled(!isGivenSelected)
        keypad.setNotesEnabled(!isGivenSelected && cursor.isSet() && !game.hasAnswer(cursor.row, cursor.col))
        keypad.setClearEnabled(!isGivenSelected && cursor.isSet() && (game.hasAnswer(cursor.row, cursor.col) || game.hasNotes(cursor.row, cursor.col)))
        keypad.disableSolvedDigits(BooleanArray(9) { game.isSolvedDigit(it + 1) })
    }

    private fun isEmptySelected(cursor: CellPosition = boardView.cursorPosition): Boolean {
        return if (cursor.isSet()) game.isEmpty(cursor.row, cursor.col) else false
    }

    private fun isGivenSelected(cursor: CellPosition = boardView.cursorPosition): Boolean {
        return if (cursor.isSet()) game.isGiven(cursor.row, cursor.col) else false
    }

    private fun cheatCell() {
        if (!game.isSolutionAvailable()) return
        val cursor = boardView.cursorPosition
        if (cursor.isSet()) {
            game.cheatCell(cursor.row, cursor.col)
            onCellChanged()
        }
    }

    private fun cheatRandomCell() {
        val cell = game.cheatRandomCell()
        if (cell.isSet()) {
            boardView.setCursor(cell.row, cell.col)
            onCellChanged()
        }
    }

    private fun setCompleted() {
        isCompleted = true
        isBookmarked = false
        showCompletedMessage()
        timer.stopUpdates()
        saveCompletedState()
        hostActivity.invalidateMenu()
        setViewsEnabled(false)
        eventBus.publish(GameResult(level, timer.getTime(), game.numberOfCheats))
    }

    private fun showCompletedMessage() {
        val completeDialog = PuzzleCompleteDialog.newInstance(timer.getTime(), game.numberOfCheats)
        parentFragmentManager.let {
            completeDialog.show(it, PuzzleCompleteDialog.TAG)
        }
    }

    override fun onRestart() {
        if (isCompleted) {
            isCompleted = false
            setViewsEnabled(true)
        }
        keypad.reset()
        game.resetProgress()
        clearCursor()
        timer.restart()
        saveRestartedState()
        hostActivity.invalidateMenu()
        if (settings.timerEnabled) {
            timer.startUpdates()
        }
    }

    private fun saveRestartedState() {
        repository.save(PuzzleSave.forRestart(puzzleId))
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun showRestartConfirmation() {
        val restartDialog = ConfirmRestartDialog()
        parentFragmentManager.let {
            restartDialog.show(it, ConfirmRestartDialog.TAG)
        }
    }

    private fun setViewsEnabled(enabled: Boolean) {
        keypad.setEnabled(enabled)
        boardView.isEnabled = enabled
    }

    private fun clearCursor() {
        boardView.clearCursor()
        boardView.invalidate()
    }
}
