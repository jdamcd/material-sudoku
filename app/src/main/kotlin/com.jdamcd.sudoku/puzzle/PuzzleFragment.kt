package com.jdamcd.sudoku.puzzle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseFragment
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
import com.jdamcd.sudoku.view.GamePuzzleView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_puzzle.*
import kotlinx.android.synthetic.main.layout_numpad_rectangle.*
import org.jetbrains.anko.design.snackbar

class PuzzleFragment : BaseFragment(), OnClickListener, OnLongClickListener, GamePuzzleView.OnCellSelectedListener, ConfirmRestartDialog.RestartContract {

    @Inject lateinit var repository: PuzzleRepository
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var settings: Settings
    @Inject lateinit var shortcuts: ShortcutController

    private lateinit var numKeys: Array<Button>

    private var puzzleId: Long = 0
    private lateinit var level: Level
    private lateinit var timer: PuzzleTimer
    private lateinit var game: Game

    private var isLoaded: Boolean = false

    private var isBookmarked: Boolean = false
    private var isCompleted: Boolean = false
    private var isEmptyCellSelected: Boolean = false
    private var isGivenSelected: Boolean = false

    private var disposable = Disposables.empty()

    private val hostActivity: PuzzleContract
        get() = activity as PuzzleContract

    internal interface PuzzleContract {
        fun setPuzzleName(name: String)
        fun setTime(time: String)
        fun invalidateMenu()
    }

    init {
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        puzzleId = requireActivity().intent.getLongExtra(PuzzleActivity.EXTRA_PUZZLE_ID, -1)
        timer = PuzzleTimer(object : UpdateCallback {
            override fun update(time: String) {
                hostActivity.setTime(time)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_puzzle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numKeys = Array(NUMKEY_IDS.size) { i -> view.findViewById<Button>(NUMKEY_IDS[i]) }
        puzzle_board.setOnCellSelectedListener(this)
        for (button in numKeys) {
            button.setOnClickListener(this)
            button.setOnLongClickListener(this)
        }
        clear_cell.setOnClickListener(this)
        note_toggle.setOnClickListener(this)
        disposable = repository.getPuzzle(puzzleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data -> setupPuzzle(data) }
    }

    private fun setupPuzzle(data: Puzzle) {
        if (!isLoaded) {
            game = data.game
        }
        puzzle_board.setGame(game)
        puzzle_board.setShowMistakes(settings.isShowErrors)

        hostActivity.setPuzzleName(data.title)
        level = data.level
        timer.setStartAt(data.time, settings.isTimerEnabled)

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
        isLoaded = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        timer.start()
        if (settings.isTimerEnabled && !isCompleted) {
            timer.startUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        timer.stopUpdates()
        if (isLoaded && !isCompleted) {
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
        repository.save(PuzzleSave(puzzleId,
                Format.stringFromGrid(game.answers),
                Format.serialiseNotes(game.notes),
                timer.getTime(),
                isBookmarked,
                game.getPercentageCorrect(),
                isCompleted,
                game.numberOfCheats))
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
        repository.save(PuzzleSave.forCompleted(puzzleId, Format.stringFromGrid(game.answers),
                timer.getTime(), game.numberOfCheats))
                .subscribeOn(Schedulers.io())
                .subscribe()
        clearResumePrompts()
    }

    private fun clearResumePrompts() {
        settings.resumePrompt = false
        shortcuts.disableResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (settings.isCheatsEnabled) {
            inflater.inflate(R.menu.fragment_puzzle_cheat, menu)
            menu.findItem(R.id.action_cheat_cell).isEnabled = !isCompleted && isEmptyCellSelected
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
        val hasNotes = isLoaded && game.hasNotes()
        clearNotes.isVisible = hasNotes && !isCompleted
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!isLoaded) return true
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
        puzzle_board.invalidate()
        hostActivity.invalidateMenu()
    }

    private fun tryUndo() {
        if (!isLoaded) return
        when {
            isCompleted -> puzzle_board.snackbar(R.string.toast_puzzle_completed)
            game.canUndo() -> undo()
            else -> puzzle_board.snackbar(R.string.toast_no_move_history)
        }
    }

    private fun undo() {
        val undoCell = game.undo()
        puzzle_board.setCursor(undoCell.row, undoCell.col)
        onCellSelected(undoCell)
        onCellChanged()
    }

    override fun onClick(v: View) {
        if (!isLoaded) return
        when (v.id) {
            R.id.keypad_1 -> setCursorCell(1)
            R.id.keypad_2 -> setCursorCell(2)
            R.id.keypad_3 -> setCursorCell(3)
            R.id.keypad_4 -> setCursorCell(4)
            R.id.keypad_5 -> setCursorCell(5)
            R.id.keypad_6 -> setCursorCell(6)
            R.id.keypad_7 -> setCursorCell(7)
            R.id.keypad_8 -> setCursorCell(8)
            R.id.keypad_9 -> setCursorCell(9)
            R.id.clear_cell -> {
                clearCell(puzzle_board.cursorPosition)
                onCellChanged()
            }
            R.id.note_toggle -> {
                note_toggle.toggle()
                invalidateUIState()
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (!isLoaded || !note_toggle.isEnabled || note_toggle.isChecked) return false
        when (v.id) {
            R.id.keypad_1 -> {
                setCursorNote(1)
                return true
            }
            R.id.keypad_2 -> {
                setCursorNote(2)
                return true
            }
            R.id.keypad_3 -> {
                setCursorNote(3)
                return true
            }
            R.id.keypad_4 -> {
                setCursorNote(4)
                return true
            }
            R.id.keypad_5 -> {
                setCursorNote(5)
                return true
            }
            R.id.keypad_6 -> {
                setCursorNote(6)
                return true
            }
            R.id.keypad_7 -> {
                setCursorNote(7)
                return true
            }
            R.id.keypad_8 -> {
                setCursorNote(8)
                return true
            }
            R.id.keypad_9 -> {
                setCursorNote(9)
                return true
            }
        }
        return false
    }

    private fun setCursorCell(value: Int) {
        val cursor = puzzle_board.cursorPosition
        if (cursor.isSet()) {
            if (note_toggle.isEnabled && note_toggle.isChecked) {
                game.toggleNote(cursor.row, cursor.col, value)
            } else {
                answerCell(cursor, value)
            }
            onCellChanged()
        } else {
            puzzle_board.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun setCursorNote(value: Int) {
        val cursor = puzzle_board.cursorPosition
        if (cursor.isSet()) {
            game.toggleNote(cursor.row, cursor.col, value)
            onCellChanged()
        } else {
            puzzle_board.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun answerCell(cursor: CellPosition, value: Int) {
        if (game.getAnswer(cursor.row, cursor.col) == value) {
            clearAnswer(cursor)
        } else {
            setAnswer(cursor, value)
        }
    }

    private fun setAnswer(cursor: CellPosition, value: Int) {
        game.setAnswer(cursor.row, cursor.col, value)
        isEmptyCellSelected = false
    }

    private fun clearAnswer(cursor: CellPosition) {
        game.setAnswer(cursor.row, cursor.col, 0)
        isEmptyCellSelected = true
    }

    private fun clearCell(cursor: CellPosition) {
        if (cursor.isSet()) {
            game.clear(cursor.row, cursor.col)
            isEmptyCellSelected = true
        } else {
            puzzle_board.snackbar(R.string.toast_no_cursor)
        }
    }

    private fun onCellChanged() {
        if (!isLoaded) return
        puzzle_board.invalidate()
        invalidateUIState()
        if (game.isCompleted()) {
            setCompleted()
        }
    }

    override fun onCellSelected(position: CellPosition) {
        if (!isLoaded) return
        isEmptyCellSelected = game.isEmpty(position.row, position.col)
        isGivenSelected = game.isGiven(position.row, position.col)
        invalidateUIState()
    }

    private fun invalidateUIState() {
        hostActivity.invalidateMenu()
        val cursor = puzzle_board.cursorPosition
        numKeys.iterator().forEach { b -> b.isEnabled = !isGivenSelected }
        note_toggle.isEnabled = !isGivenSelected && cursor.isSet() && !game.hasAnswer(cursor.row, cursor.col)
        clear_cell.isEnabled = !isGivenSelected && cursor.isSet() && (game.hasAnswer(cursor.row, cursor.col) || game.hasNotes(cursor.row, cursor.col))
        if (!isGivenSelected && !note_toggle.isChecked) {
            for (i in 0..8) {
                numKeys[i].isEnabled = !game.isSolvedDigit(i + 1)
            }
        }
    }

    private fun cheatCell() {
        if (!game.isSolutionAvailable()) return
        val cursor = puzzle_board.cursorPosition
        if (cursor.isSet()) {
            game.cheatCell(cursor.row, cursor.col)
            isEmptyCellSelected = false
            onCellChanged()
        }
    }

    private fun cheatRandomCell() {
        val cell = game.cheatRandomCell()
        if (cell.isSet()) {
            puzzle_board.setCursor(cell.row, cell.col)
            isEmptyCellSelected = false
            isGivenSelected = false
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
        game.resetProgress()
        clearCursor()
        timer.restart()
        saveRestartedState()
        hostActivity.invalidateMenu()
        if (settings.isTimerEnabled) {
            timer.startUpdates()
        }
    }

    private fun saveRestartedState() {
        repository.save(PuzzleSave.forRestart(puzzleId))
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun setViewsEnabled(enabled: Boolean) {
        numKeys.iterator().forEach { b -> b.isEnabled = enabled }
        note_toggle.isEnabled = enabled
        clear_cell.isEnabled = enabled
        puzzle_board.isEnabled = enabled
    }

    private fun showRestartConfirmation() {
        val restartDialog = ConfirmRestartDialog()
        parentFragmentManager.let {
            restartDialog.show(it, ConfirmRestartDialog.TAG)
        }
    }

    private fun clearCursor() {
        puzzle_board.clearCursor()
        puzzle_board.invalidate()
    }

    companion object {
        private val NUMKEY_IDS = intArrayOf(R.id.keypad_1, R.id.keypad_2, R.id.keypad_3, R.id.keypad_4, R.id.keypad_5, R.id.keypad_6, R.id.keypad_7, R.id.keypad_8, R.id.keypad_9)
    }
}
