package com.example.mykatatictac

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mykatatictac.model.Board
import com.example.mykatatictac.presenter.Presenter
import com.example.mykatatictac.ui.theme.MyKataTicTacTheme

class MainActivity : ComponentActivity() {
    private val presenter: Presenter by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyKataTicTacTheme {
                Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
                    createLayout(presenter)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun createLayout(presenter: Presenter) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val t = remember { AppFlow._state }
        val items = remember { presenter.items }
        Column(
            Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when (t.value) {
                    AppFlow.State.Init -> "Welcome to my TicTacToe ;)"
                    AppFlow.State.Player1 -> "Player ONE turn! (X)"
                    AppFlow.State.Player2 -> "Player TWO turn! (0)"
                    AppFlow.State.Result -> "Game over. "
                },
                modifier = Modifier.background(Color(0xFFAABBCC)).wrapContentSize().padding(20.dp),
                color = White,
                fontSize = 24.sp
            )
        }
        Column(
            Modifier.fillMaxSize().weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                createCellRow(0, presenter, items)
                createCellRow(1, presenter, items)
                createCellRow(2, presenter, items)
            }
        }
        Row(
            Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier.padding(20.dp),
                onClick = {
                    presenter.initializeBoard()
                    AppFlow.go(AppFlow.State.Player1)
                }
            ) {
                Text(
                    text = when (t.value) {
                        AppFlow.State.Init,
                        AppFlow.State.Result -> "Start new game"

                        AppFlow.State.Player1,
                        AppFlow.State.Player2 -> "Restart"
                    },
                )
            }
            Button(
                modifier = Modifier.padding(20.dp),
                onClick = {
                    (context as Activity).finish()
                    AppFlow.go(AppFlow.State.Init)
                }
            ) {
                Text(
                    text = "Exit"
                )
            }
        }
    }
}

@Composable
fun createCellRow(i: Int, presenter: Presenter, items: SnapshotStateList<Board.CellState?>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        createCell(i * 3, presenter, items)
        createCell(i * 3 + 1, presenter, items)
        createCell(i * 3 + 2, presenter, items)
    }
}


@Composable
fun createCell(n: Int, presenter: Presenter, items: SnapshotStateList<Board.CellState?>) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = when (items[n]) {
                null -> White
                Board.CellState.X -> Color.Yellow
                Board.CellState.O -> Color.Green
                Board.CellState.I -> White
            }
        ),
        onClick = {
            presenter.onPlayerTurn(n)
        },
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(10.dp)
            .background(White)
            .border(2.dp, Color.Gray)
            .padding(5.dp)
    ) {
        Text(
            text = (items[n]?.name ?: "").replace("I", "*"),
            color = Color.Black,
            fontSize = 24.sp
        )
    }
}
