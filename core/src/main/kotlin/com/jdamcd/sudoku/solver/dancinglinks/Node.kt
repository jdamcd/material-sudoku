package com.jdamcd.sudoku.solver.dancinglinks

@Suppress("LeakingThis")
internal open class Node internal constructor(val rowNumber: Int, val label: Int, var column: ColumnNode?) {

    var left: Node? = this
    var right: Node? = this
    var up: Node? = this
    var down: Node? = this
}
