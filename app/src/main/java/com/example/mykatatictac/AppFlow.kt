package com.example.mykatatictac

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.mykatatictac.model.Board.CellState

class AppFlow {
    enum class State { Init, Player1, Player2, Result }


    companion object {
        var state = State.Init
        var player: CellState = CellState.X // use of the same enum for simplicity
        val _state = mutableStateOf(state)

        fun go(st: State? = null) {
            st?.let { state = st }
            Log.d("App", "==== App state changed: $state")

            // update UI
            _state.value = state

            when (state) {
                State.Init -> {
                    //state = State.Player1
                    //player = CellState.Init
                }

                State.Player1 -> {
                    state = State.Player2
                    player = CellState.X
                }

                State.Player2 -> {
                    //presenter.onPlayerTurn()
                    state = State.Player1
                    player = CellState.O
                }

                State.Result -> {

                }
            }

        }

        fun isGameStarted(): Boolean {
            return state == State.Player1 || state == State.Player2
        }

    }


}