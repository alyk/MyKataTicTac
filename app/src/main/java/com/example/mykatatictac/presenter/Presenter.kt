package com.example.mykatatictac.presenter

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykatatictac.GameFlow
import com.example.mykatatictac.model.Board.CellState
import com.example.mykatatictac.types.State
import com.example.mykatatictac.types.TurnResult
import com.example.mykatatictac.types.UiAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

open class Presenter : ViewModel() {
    private val channel = Channel<UiAction>()
    private val channelTurnCellNum = Channel<Int>()
    var items: SnapshotStateList<CellState?> = (0..8).map { null }.toMutableStateList()
    var screenTitle: MutableState<String> = mutableStateOf("")
    var actionButtonText: MutableState<String> = mutableStateOf("...")

    private val stateChangeUpdate: (State) -> Unit = { state ->
        when (state) {
            State.Welcome -> {
                screenTitle.value = "Welcome to \nsuper game ;)\n" +
                        "\nclick Start to begin"
                actionButtonText.value = "Start"
            }

            State.Init -> {
                screenTitle.value = "app is loading..."
            }

            State.Player1,
            State.Player2 -> {
                screenTitle.value = "$state' turn!"
                actionButtonText.value = "Restart"
            }

            State.Result -> {
                screenTitle.value = "Game over!\n" +
                        "\nwho's winner?"
                actionButtonText.value = "Start anew"
            }

            State.Error -> TODO()
        }

    }
    private val updateGameResult: (TurnResult) -> Unit = { result ->
        screenTitle.value = if (result == TurnResult.Draw) "game is a draw!" else "$result won!"
    }

    private val gameFlow: GameFlow by lazy {
        GameFlow(
            channel,
            channelTurnCellNum,
            stateChangeUpdate,
            updateGameResult,
            items
        )
    }

    init {
        viewModelScope.launch {
            gameFlow.initGameFlow()
        }
    }

    fun onPlayerTurn(n: Int) {
        viewModelScope.launch {
            if (gameFlow.isGameStarted)
                channelTurnCellNum.send(n)
        }
    }

    fun sendUserAction(uiAction: UiAction) {
        viewModelScope.launch {
            if (gameFlow.isFlowInitialized)
                channel.send(uiAction)
        }
    }
}


