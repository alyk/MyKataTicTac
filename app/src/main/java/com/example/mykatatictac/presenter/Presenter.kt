package com.example.mykatatictac.presenter

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.mykatatictac.AppFlow
import com.example.mykatatictac.model.Board
import com.example.mykatatictac.model.Board.CellState

open class Presenter : ViewModel() {
    private var board = Board()
    var items: SnapshotStateList<CellState?> = board.getCellStates().map { null }.toMutableStateList()

    fun initializeBoard() {
        board = Board()
        items.forEachIndexed { idx, _ -> items[idx] = CellState.I }
    }

    fun onPlayerTurn(n: Int) {
        if (!AppFlow.isGameStarted()) return

        if (board.isDirtyCell(n)) return

        board.doTurn(n, AppFlow.player)?.let {
            // win
            items[n] = AppFlow.player
            AppFlow.go(AppFlow.State.Result)
            return
        }

        items[n] = AppFlow.player
        if (board.isAllDirty()) {
            // game is a draw
            AppFlow.go(AppFlow.State.Result)
            return
        }

        AppFlow.go()
    }

}