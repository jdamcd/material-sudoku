package com.jdamcd.sudoku.game

import com.jdamcd.sudoku.game.history.Answer
import com.jdamcd.sudoku.game.history.Clear
import com.jdamcd.sudoku.game.history.Move
import com.jdamcd.sudoku.game.history.Note
import com.jdamcd.sudoku.util.Format
import com.jdamcd.sudoku.util.Validate
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedList
import java.util.Random
import java.util.Stack

class Game {

    val sudoku: Sudoku
    var answers: Array<IntArray>
    var notes: Array<IntArray>
    var numberOfCheats: Int = 0
    private var moves = Stack<Move>()

    constructor(puzzle: Sudoku) {
        this.sudoku = puzzle
        answers = Format.deepCopy(puzzle.givens)
        notes = Array(9) { IntArray(9) }
    }

    constructor(puzzle: Sudoku, answers: Array<IntArray>) {
        this.sudoku = puzzle
        this.answers = answers
        notes = Array(9) { IntArray(9) }
    }

    constructor(puzzle: Sudoku, answers: Array<IntArray>, notes: Array<IntArray>) {
        this.sudoku = puzzle
        this.answers = answers
        this.notes = notes
    }

    fun isSolutionAvailable(): Boolean = sudoku.isSolution

    fun getPercentageCorrect(): Int = getNumberOfCorrectAnswers() * 100 / getAnswersNeeded()

    fun getNumberOfCorrectAnswers(): Int {
        if (!sudoku.isSolution) return 0

        val givens = Format.arrayFromGrid(sudoku.givens)
        val solution = Format.arrayFromGrid(sudoku.solution!!)
        val user = Format.arrayFromGrid(answers)

        return givens.indices.count { givens[it] == 0 && solution[it] == user[it] }
    }

    fun getAnswersNeeded(): Int {
        val givens = Format.arrayFromGrid(sudoku.givens)
        return givens.count { it == 0 }
    }

    fun isStarted(): Boolean = !Arrays.deepEquals(answers, sudoku.givens)

    fun isCompleted(): Boolean = sudoku.isSolution && Arrays.deepEquals(answers, sudoku.solution)

    fun getGiven(row: Int, col: Int): Int = sudoku.getCellValue(row, col)

    fun getAnswer(row: Int, col: Int): Int = answers[row][col]

    fun isEmpty(row: Int, col: Int): Boolean = answers[row][col] == 0

    fun isGiven(row: Int, col: Int): Boolean = sudoku.getCellValue(row, col) != 0

    fun getSolution(row: Int, col: Int): Int = sudoku.getSolutionCellValue(row, col)

    fun setAnswer(row: Int, col: Int, value: Int) {
        moves.push(Answer(CellPosition(row, col), answers[row][col]))
        answers[row][col] = value
    }

    fun isSolvedDigit(digit: Int): Boolean {
        var count = 0
        for (row in 0..8) {
            for (col in 0..8) {
                if (getAnswer(row, col) == digit) {
                    count++
                }
            }
        }
        return count == 9
    }

    fun canUndo(): Boolean = !moves.empty()

    fun undo(): CellPosition {
        val move = moves.pop()
        val position = move.position
        move.undo(this)
        return position
    }

    fun hasAnswer(row: Int, col: Int): Boolean = answers[row][col] != 0

    fun toggleNote(row: Int, col: Int, value: Int) {
        val note = notes[row][col]
        moves.push(Note(CellPosition(row, col), note))
        notes[row][col] = note xor (1 shl value)
    }

    fun hasNotes(row: Int, col: Int): Boolean = notes[row][col] != 0

    fun hasNote(row: Int, col: Int, value: Int): Boolean {
        val noteFlags = getNoteFlags(notes[row][col])
        return noteFlags[value]
    }

    fun getNotes(row: Int, col: Int): IntArray {
        val noteFlags = getNoteFlags(notes[row][col])

        val noteDigits = noteFlags.indices.filterTo(LinkedList()) { noteFlags[it] }
        return toArray(noteDigits)
    }

    private fun toArray(intList: LinkedList<Int>): IntArray {
        val boxedArray = intList.toTypedArray()
        val len = boxedArray.size
        val array = IntArray(len)
        for (i in 0 until len) {
            array[i] = boxedArray[i]
        }
        return array
    }

    fun clear(row: Int, col: Int) {
        moves.push(Clear(CellPosition(row, col), answers[row][col], notes[row][col]))
        answers[row][col] = 0
        notes[row][col] = 0
    }

    fun hasNotes(): Boolean = !Arrays.deepEquals(notes, Array(9) { IntArray(9) })

    fun clearNotes() {
        notes = Array(9) { IntArray(9) }
        removeNotesFromHistory()
    }

    private fun removeNotesFromHistory() {
        val removals = LinkedList<Move>()
        for (move in moves) {
            if (move is Note) {
                removals.add(move)
            } else if (move is Clear) {
                if (move.hasOldValue()) {
                    move.removeOldNote()
                } else {
                    removals.add(move)
                }
            }
        }
        moves.removeAll(removals)
    }

    private fun getNoteFlags(note: Int): BooleanArray {
        val bits = BooleanArray(10)
        for (i in bits.indices) {
            bits[i] = note and (1 shl i) != 0
        }
        return bits
    }

    fun cheatCell(row: Int, col: Int) {
        setAnswer(row, col, getSolution(row, col))
        numberOfCheats++
    }

    fun cheatRandomCell(): CellPosition {
        val unanswered = findUnansweredCells()
        return if (unanswered.isEmpty()) {
            CellPosition()
        } else {
            val cell = unanswered[Random().nextInt(unanswered.size)]
            cheatCell(cell.row, cell.col)
            cell
        }
    }

    private fun findUnansweredCells(): ArrayList<CellPosition> {
        val unanswered = ArrayList<CellPosition>()
        for (row in 0..8) {
            for (col in 0..8) {
                if (getAnswer(row, col) == 0) {
                    unanswered.add(CellPosition(row, col))
                }
            }
        }
        return unanswered
    }

    fun cheatAll() {
        answers = Format.deepCopy(sudoku.solution!!)
    }

    fun isValidColumn(row: Int, col: Int): Boolean = Validate.isValidColumn(row, col, answers)

    fun isValidRow(row: Int, col: Int): Boolean = Validate.isValidRow(row, col, answers)

    fun isValidBox(row: Int, col: Int): Boolean = Validate.isValidBox(row, col, answers)

    fun resetProgress() {
        answers = Format.deepCopy(sudoku.givens)
        clearNotes()
        numberOfCheats = 0
        moves = Stack()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Game

        return sudoku == other.sudoku &&
            answers contentDeepEquals other.answers &&
            notes contentDeepEquals other.notes &&
            numberOfCheats == other.numberOfCheats
    }

    override fun hashCode(): Int {
        var result = sudoku.hashCode()
        result = 31 * result + answers.contentDeepHashCode()
        result = 31 * result + notes.contentDeepHashCode()
        result = 31 * result + numberOfCheats.hashCode()
        return result
    }
}
