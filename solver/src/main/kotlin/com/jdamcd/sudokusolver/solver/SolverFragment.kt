package com.jdamcd.sudokusolver.solver

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jdamcd.sudoku.game.CellPosition
import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudoku.util.Format
import com.jdamcd.sudokusolver.R
import com.jdamcd.sudokusolver.about.AboutActivity
import com.jdamcd.sudokusolver.solver.view.InteractivePuzzleView
import com.jdamcd.sudokusolver.solver.view.InteractivePuzzleView.OnCellSelectedListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

class SolverFragment : Fragment(), OnClickListener, OnCellSelectedListener {

    private var puzzle: Sudoku? = null
    private lateinit var puzzleView: InteractivePuzzleView
    private lateinit var clearButton: ImageButton

    private var disposable = Disposables.empty()

    private var isSolved: Boolean = false

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_solver, container, false)
        puzzleView = layout.findViewById(R.id.puzzle_board)
        puzzleView.setOnCellSelectedListener(this)
        clearButton = layout.findViewById(R.id.clear_cell)
        setButtonListeners(layout)
        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    private fun setButtonListeners(layout: View) {
        for (id in KEY_IDS) {
            layout.findViewById<View>(id).setOnClickListener(this)
        }
    }

    private fun setKeypadEnabled(enabled: Boolean) {
        for (id in KEY_IDS) {
            view?.findViewById<View>(id)?.isEnabled = enabled
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupPuzzle()
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            clearButton.isEnabled = false
        }
    }

    private fun restoreState(savedInstanceState: Bundle) {
        isSolved = savedInstanceState.getBoolean(STATE_SOLVED)
        puzzle = Sudoku(savedInstanceState.getIntArray(STATE_PUZZLE)!!)
        if (isSolved) {
            puzzle?.setSolution(savedInstanceState.getIntArray(STATE_SOLUTION)!!)
            setViewsEnabled(!isSolved)
        }
        puzzleView.setPuzzle(puzzle!!)
        updateClearButtonState()
    }

    private fun setupPuzzle() {
        puzzleView.requestFocus()
        puzzle = Sudoku()
        puzzleView.setPuzzle(puzzle!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SOLVED, isSolved)
        outState.putIntArray(STATE_PUZZLE, Format.arrayFromGrid(puzzle!!.givens))
        if (isSolved) {
            outState.putIntArray(STATE_SOLUTION, Format.arrayFromGrid(puzzle!!.solution!!))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_solver, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.action_clear -> clearPuzzle()
            R.id.action_solve ->
                if (isSolved) {
                    showMessage(R.string.toast_solved)
                } else {
                    solve()
                }
            R.id.action_about -> startActivity(Intent(activity, AboutActivity::class.java))
        }
        return true
    }

    private fun clearPuzzle() {
        puzzle = Sudoku()
        puzzleView.setPuzzle(puzzle!!)
        isSolved = false
        setViewsEnabled(true)
        clearButton.isEnabled = false
    }

    override fun onClick(v: View) {
        if (isSolved) {
            showMessage(R.string.toast_solved)
        } else {
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
                R.id.clear_cell -> setCursorCell(0)
            }
        }
    }

    private fun setCursorCell(value: Int) {
        val cursor = puzzleView.getCursorPosition()
        if (cursor.isSet()) {
            if (isCurrentCellValue(cursor.row, cursor.col, value)) {
                puzzle?.setCellValue(cursor.row, cursor.col, 0)
            } else {
                puzzle?.setCellValue(cursor.row, cursor.col, value)
            }
            onCellChanged()
        } else {
            showMessage(R.string.toast_no_cursor)
        }
    }

    private fun isCurrentCellValue(row: Int, col: Int, value: Int): Boolean {
        return puzzle?.getCellValue(row, col) == value
    }

    override fun onCellSelected(position: CellPosition) {
        updateClearButtonState()
    }

    private fun onCellChanged() {
        puzzleView.invalidate()
        updateClearButtonState()
    }

    private fun updateClearButtonState() {
        val cell = puzzleView.getCursorPosition()
        clearButton.isEnabled = cell.isSet() && puzzle!!.getCellValue(cell.row, cell.col) != 0
    }

    private fun setViewsEnabled(enabled: Boolean) {
        setKeypadEnabled(enabled)
        puzzleView.isEnabled = enabled
    }

    private fun solve() {
        disposable = Single.fromCallable { puzzle?.solve() }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    setViewsEnabled(false)
                    resetCursor()
                },
                {
                    showMessage(R.string.toast_no_solution)
                    resetCursor()
                }
            )
    }

    private fun resetCursor() {
        puzzleView.clearCursor()
        puzzleView.invalidate()
    }

    private fun showMessage(@StringRes messageId: Int) {
        view?.let {
            Snackbar.make(it, messageId, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val STATE_PUZZLE = "puzzleData"
        private const val STATE_SOLUTION = "solution"
        private const val STATE_SOLVED = "solved"

        private val KEY_IDS = intArrayOf(R.id.keypad_1, R.id.keypad_2, R.id.keypad_3, R.id.keypad_4, R.id.keypad_5, R.id.keypad_6, R.id.keypad_7, R.id.keypad_8, R.id.keypad_9, R.id.clear_cell)
    }
}
