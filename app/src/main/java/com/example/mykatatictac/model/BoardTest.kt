package com.example.mykatatictac.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class BoardTest {

    @org.junit.jupiter.api.Test
    fun checkTurn() {
        val board = Board()
        assertNull(board.updateBoard(1, Board.CellState.X))
        assertNull(board.updateBoard(2, Board.CellState.O))
        assertNull(board.updateBoard(4, Board.CellState.X))
        assertNull(board.updateBoard(3, Board.CellState.O))
        assertNull(board.updateBoard(5, Board.CellState.X))
        assertNull(board.updateBoard(6, Board.CellState.O))
        assertEquals(board.updateBoard(7, Board.CellState.X), Board.CellState.X)
    }
}