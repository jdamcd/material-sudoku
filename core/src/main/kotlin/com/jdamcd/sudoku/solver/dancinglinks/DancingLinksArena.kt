package com.jdamcd.sudoku.solver.dancinglinks

internal class DancingLinksArena constructor(labels: IntArray) {

    private val firstColumn: ColumnNode
    private val solution: Array<Node?>
    private var rowCount: Int = 0
    private var solutionIndex: Int = 0
    private var startingCount: Int = 0
    private var traveller: Node? = null

    init {
        val columns = arrayOfNulls<ColumnNode>(labels.size)

        for (i in labels.indices) {
            // No optional constraints in this case
            columns[i] = ColumnNode(labels[i])
            columns[i]?.right = null

            if (i > 0) {
                columns[i]?.left = columns[i - 1]
                columns[i - 1]?.right = columns[i]
            }
        }

        firstColumn = ColumnNode(0)
        columns[0]?.left = firstColumn
        firstColumn.right = columns[0]
        columns[labels.size - 1]?.right = firstColumn
        firstColumn.left = columns[labels.size - 1]
        solution = arrayOfNulls(labels.size)
        solutionIndex = 0
    }

    private fun getNextColumn(): ColumnNode {
        var next = firstColumn
        var best = firstColumn
        var bestCount = Integer.MAX_VALUE

        while (bestCount > 0) {
            next = next.right?.column!!

            if (next == firstColumn) {
                break
            } else if (next.count < bestCount) {
                best = next
                bestCount = next.count
            }
        }

        return best // Column with fewest rows
    }

    private fun removeRow(rowHead: Node) {
        var scanner: Node? = rowHead

        do {
            val next = scanner!!.right
            removeColumn(scanner.column)
            scanner = next
        } while (scanner !== rowHead)
    }

    private fun reinsertRow(rowHead: Node?) {
        var scanner = rowHead

        do {
            scanner = scanner!!.left
            reinsertColumn(scanner!!.column)
        } while (scanner !== rowHead)
    }

    private fun removeColumn(columnHead: ColumnNode?) {
        var scanner = columnHead!!.down

        // Unsnap elements for each row in column
        while (scanner !== columnHead) {
            var rowTraveller = scanner!!.right

            // Remove row
            while (rowTraveller !== scanner) {
                rowTraveller!!.up?.down = rowTraveller.down
                rowTraveller.down?.up = rowTraveller.up
                rowTraveller.column?.decrement()
                rowTraveller = rowTraveller.right
            }

            scanner = scanner.down
        }

        // Remove column
        columnHead.left?.right = columnHead.right
        columnHead.right?.left = columnHead.left
    }

    private fun reinsertColumn(columnNode: ColumnNode?) {
        var scanner = columnNode!!.up

        while (scanner !== columnNode) {
            var rowTraveller = scanner!!.left

            while (rowTraveller !== scanner) {
                rowTraveller!!.up?.down = rowTraveller
                rowTraveller.down?.up = rowTraveller
                rowTraveller.column?.increment()
                rowTraveller = rowTraveller.left
            }

            scanner = scanner.up
        }

        // Return column to header list
        columnNode.left?.right = columnNode
        columnNode.right?.left = columnNode
    }

    fun addInitialRow(labels: IntArray): Node? {
        var result: Node? = null

        if (labels.isNotEmpty()) {
            var prev: Node? = null
            var first: Node? = null

            rowCount++

            for (label in labels) {
                val node: Node
                var searcher: ColumnNode
                var col: ColumnNode? = null

                searcher = firstColumn
                do {
                    if (searcher.label == label)
                        col = searcher

                    searcher = searcher.right as ColumnNode
                } while (searcher != firstColumn && col == null)

                node = Node(rowCount, label, col)
                col?.addEndNode(node)
                node.left = prev
                node.right = null

                if (prev != null) {
                    prev.right = node
                } else {
                    first = node
                }
                prev = node
            }

            // Link circular list
            first?.left = prev
            prev?.right = first
            result = first
        }

        return result
    }

    fun solve(): IntArray? {
        var result: IntArray? = null // Returns null if there is no solution

        // Initialise traveller, which moves depth first column to column
        if (traveller == null) {
            traveller = getNextColumn().down
            startingCount = solutionIndex
        }

        // Travel until solution is found or the whole space was covered
        while (result == null) {
            val thisColumn = traveller?.column

            // First column comes after last, since they're linked in a circular way
            if (thisColumn == firstColumn || thisColumn == traveller) {
                // Moved past last row or column - solution?
                if (thisColumn == firstColumn) {
                    result = generatePartialSolutionIndices()
                }

                // Go left and traverse down column to left, unless we've returned to the start
                if (solutionIndex == startingCount) {
                    return result
                } else {
                    traveller = solution[--solutionIndex] // Pop
                    reinsertRow(traveller)
                    traveller = traveller?.down
                }
            } else {
                // Push deeper to the right
                removeRow(traveller!!)
                solution[solutionIndex++] = traveller!!
                traveller = getNextColumn().down
            }
        }

        return result
    }

    private fun generatePartialSolutionIndices(): IntArray {
        val result = IntArray(solutionIndex)
        for (i in 0 until solutionIndex) {
            result[i] = solution[i]!!.rowNumber - 1
        }
        return result
    }

    // Remove already known portions of the solution space
    fun removeInitialSolutionSet(solutions: List<Node>): Boolean {

        for (row in solutions) {
            if (row.down?.up !== row) {
                // Row was removed from arena - inconsistent
                return false
            }

            removeRow(row)
            solution[solutionIndex++] = row
        }
        return true
    }
}
