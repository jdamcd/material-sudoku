package com.jdamcd.sudoku.puzzle

import android.view.View
import android.widget.Button
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.view.CheckableImageButton

internal class PuzzleKeypad(
    root: View,
    val onSetValue: (Int) -> Unit,
    val onSetNote: (Int) -> Unit,
    val onClearValue: () -> Unit,
    val onToggleNotes: () -> Unit
) : View.OnClickListener, View.OnLongClickListener {

    private var clear: View = root.findViewById(R.id.clear_cell)
    private var notes: CheckableImageButton = root.findViewById(R.id.note_toggle)
    private var numberKeys: Array<Button>

    init {
        numberKeys = Array(NUMKEY_IDS.size) { i -> root.findViewById(NUMKEY_IDS[i]) }
    }

    fun setupListeners() {
        for (button in numberKeys) {
            button.setOnClickListener(this)
            button.setOnLongClickListener(this)
        }
        clear.setOnClickListener(this)
        notes.setOnClickListener(this)
    }

    fun setNumbersEnabled(enabled: Boolean) {
        numberKeys.forEach { it.isEnabled = enabled }
    }

    fun setNotesEnabled(enabled: Boolean) {
        notes.isEnabled = enabled
    }

    fun setClearEnabled(enabled: Boolean) {
        clear.isEnabled = enabled
    }

    fun setEnabled(enabled: Boolean) {
        setNumbersEnabled(enabled)
        setNotesEnabled(enabled)
        setClearEnabled(enabled)
    }

    fun disableSolvedDigits(completedDigits: BooleanArray) {
        if (!notes.isChecked) {
            completedDigits.forEachIndexed { i, solved ->
                if (solved) numberKeys[i].isEnabled = false
            }
        }
    }

    fun isNotesMode() = notes.isEnabled && notes.isChecked

    override fun onClick(v: View) {
        when (v.id) {
            R.id.keypad_1 -> onSetValue(1)
            R.id.keypad_2 -> onSetValue(2)
            R.id.keypad_3 -> onSetValue(3)
            R.id.keypad_4 -> onSetValue(4)
            R.id.keypad_5 -> onSetValue(5)
            R.id.keypad_6 -> onSetValue(6)
            R.id.keypad_7 -> onSetValue(7)
            R.id.keypad_8 -> onSetValue(8)
            R.id.keypad_9 -> onSetValue(9)
            R.id.clear_cell -> onClearValue()
            R.id.note_toggle -> {
                notes.toggle()
                onToggleNotes()
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (!notes.isEnabled || notes.isChecked) return false
        when (v.id) {
            R.id.keypad_1 -> {
                onSetNote(1)
                return true
            }
            R.id.keypad_2 -> {
                onSetNote(2)
                return true
            }
            R.id.keypad_3 -> {
                onSetNote(3)
                return true
            }
            R.id.keypad_4 -> {
                onSetNote(4)
                return true
            }
            R.id.keypad_5 -> {
                onSetNote(5)
                return true
            }
            R.id.keypad_6 -> {
                onSetNote(6)
                return true
            }
            R.id.keypad_7 -> {
                onSetNote(7)
                return true
            }
            R.id.keypad_8 -> {
                onSetNote(8)
                return true
            }
            R.id.keypad_9 -> {
                onSetNote(9)
                return true
            }
        }
        return false
    }

    companion object {
        private val NUMKEY_IDS = intArrayOf(
            R.id.keypad_1,
            R.id.keypad_2,
            R.id.keypad_3,
            R.id.keypad_4,
            R.id.keypad_5,
            R.id.keypad_6,
            R.id.keypad_7,
            R.id.keypad_8,
            R.id.keypad_9
        )
    }
}
