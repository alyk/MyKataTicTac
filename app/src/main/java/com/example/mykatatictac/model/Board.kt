package com.example.mykatatictac.model

class Board {
    /*
    0 1 2
    3 4 5
    6 7 8
    */
    enum class CellState { I, X, O }

    private val cells: MutableList<CellState?> = (0..8).map { (null) }.toMutableList()

    private val triples = listOf(
        // rows
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        // columns
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        // diagonals
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    var dirtyNum = 0

    private fun checkTurn(): CellState? {
        val match = triples.firstOrNull { triple ->
            when {
                cells[triple[0]] == null -> false
                cells[triple[0]] != cells[triple[1]] -> false
                cells[triple[1]] != cells[triple[2]] -> false
                else -> true
            }
        }
        return if (match == null) null else cells[match[0]]
    }

    fun doTurn(idx: Int, state: CellState): CellState? {
        cells[idx] = state
        dirtyNum++
        return checkTurn()
    }

    fun getCellState(n: Int): CellState? {
        return cells[n]

    }

    fun isDirtyCell(n: Int): Boolean {
        return cells[n] == CellState.X || cells[n] == CellState.O
    }

    fun getCellStates(): MutableList<CellState?> {
        return cells
    }

    fun isAllDirty(): Boolean {
        return dirtyNum > 8
    }
}



