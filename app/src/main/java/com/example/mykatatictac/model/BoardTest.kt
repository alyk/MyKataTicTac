package com.example.mykatatictac.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class BoardTest {

    @org.junit.jupiter.api.Test
    fun checkTurn() {
        val board = Board()
        assertNull(board.doTurn(1, Board.CellState.X))
        assertNull(board.doTurn(2, Board.CellState.O))
        assertNull(board.doTurn(4, Board.CellState.X))
        assertNull(board.doTurn(3, Board.CellState.O))
        assertNull(board.doTurn(5, Board.CellState.X))
        assertNull(board.doTurn(6, Board.CellState.O))
        assertEquals(board.doTurn(7, Board.CellState.X), Board.CellState.X)
    }
}