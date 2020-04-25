package com.jdamcd.sudoku.solver.dancinglinks

internal class ColumnNode(label: Int) : Node(0, label, null) {

    var count: Int = 0 // Rows with 1 in column
        private set

    init {
        this.column = this
        count = 0
    }

    // Node for row containing 1 in this column, added below
    fun addEndNode(node: Node) {
        val end = up
        node.up = end
        node.down = this
        end!!.down = node
        this.up = node
        count++
    }

    fun increment() {
        count++
    }

    fun decrement() {
        count--
    }
}
