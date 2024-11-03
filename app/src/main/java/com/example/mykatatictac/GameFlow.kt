package com.example.mykatatictac

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mykatatictac.model.Board
import com.example.mykatatictac.model.Board.CellState
import com.example.mykatatictac.types.State
import com.example.mykatatictac.types.TurnResult
import com.example.mykatatictac.types.UiAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameFlow(
    private val channel: Channel<UiAction>,
    private val channelTurnAction: Channel<Int>,
    private var stateChangeUpdate: (State) -> Unit,
    private var gameResult: (TurnResult) -> Unit,
    private var itemUpdates: SnapshotStateList<CellState?>
) {
    var isGameStarted: Boolean = false
    var isFlowInitialized: Boolean = false
    private var state = State.Init
    private var board: Board = Board()

    private val playerCell: CellState?
        get() {
            return when (state) {
                State.Player1 -> CellState.X
                State.Player2 -> CellState.O
                else -> null
            }
        }

    private val turnResult: TurnResult
        get() {
            return when (state) {
                State.Player1 -> TurnResult.Player1
                State.Player2 -> TurnResult.Player2
                else -> TurnResult.Next
            }
        }

    fun initializeBoard() {
        board = Board()
        notifyBoardUpdated()
    }

    val onStateChanged: ((State) -> Unit) = {
        state = it
        stateChangeUpdate.invoke(it)
        Log.d("GameFlow", "==== Game state changed: $state")
    }

    suspend fun initGameFlow() {
        onStateChanged.invoke(State.Init)
        delay(2000)
        isFlowInitialized = true
        withContext(Dispatchers.Main) {
            launch {
                for (action in channel) {
                    Log.d("Channel", "UI action: $action")
                    when (action) {
                        UiAction.Restart,
                        UiAction.NewGame -> {
                            launch {
                                val result = runNewGame()
                                showResult(result)
                            }
                        }
                        else -> {}
                    }
                }
                isFlowInitialized = true
            }
            launch {
                showWelcome() // init UI
            }
        }
    }

    private suspend fun showResult(result: TurnResult) {
        notifyBoardUpdated()
        onStateChanged.invoke(State.Result)
        delay(3000)
        notifyGameResult(result)
    }

    private fun notifyGameResult(result: TurnResult) {
        gameResult.invoke(result)
    }

    private fun notifyBoardUpdated(nulState: Boolean = false) {
        board.getCellStates().forEachIndexed { idx, state -> itemUpdates[idx] = if (nulState) null else state }
    }

    private fun showWelcome() {
        onStateChanged.invoke(State.Welcome)
    }

    private suspend fun runNewGame(): TurnResult {
        onStateChanged(State.Player1)
        initializeBoard()

        isGameStarted = true

        for (n in channelTurnAction) {
            Log.d("Turn", "$playerCell --> $n")

            if (board.isDirtyCell(n)) continue

            board.updateBoard(n, playerCell!!)?.let {
                isGameStarted = false
                return turnResult
            }.also {
                notifyBoardUpdated()
            }

            if (board.isAllDirty()) {
                return TurnResult.Draw
            }
            onStateChanged(if (state == State.Player1) State.Player2 else State.Player1)
        }
        return TurnResult.Error
    }
}