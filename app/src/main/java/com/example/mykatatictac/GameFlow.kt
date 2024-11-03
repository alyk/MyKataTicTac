package com.example.mykatatictac

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mykatatictac.model.Board
import com.example.mykatatictac.model.Board.CellState
import com.example.mykatatictac.types.State
import com.example.mykatatictac.types.TurnResult
import com.example.mykatatictac.types.UiAction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

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
        println("=== Board initialized!")
        notifyBoardUpdated()
    }

    val onStateChanged: ((State) -> Unit) = {
        state = it
        stateChangeUpdate.invoke(it)
        println("<-- Game state changed: $state")
    }

    suspend fun initGameFlow() {
        onStateChanged.invoke(State.Init)
        delay(2000)
        isFlowInitialized = true
        withContext(Dispatchers.Main) {
            launch {
                for (action in channel) {
                    println("--> UI action: $action")
                    when (action) {
                        UiAction.Restart,
                        UiAction.NewGame -> {
                            launch {
                                val result = runNewGame()
                                showResult(result)
                            }
                        }
                        UiAction.Exit -> cancel()
                        else -> {}
                    }
                }
                isFlowInitialized = true
            }
            launch {
                showWelcome() // init first UI screen
            }
        }
    }

    private suspend fun showResult(result: TurnResult) {
        notifyBoardUpdated()
        onStateChanged.invoke(State.Result)
        // show additional screen
        // delay(3000)
        notifyGameResult(result)
    }

    private fun notifyGameResult(result: TurnResult) {
        println("<-- UI game Result!")
        gameResult.invoke(result)
    }

    private fun notifyBoardUpdated(nulState: Boolean = false) {
        println("<-- UI board update!")
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
            println("--> turn: $state [ $n ]")

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