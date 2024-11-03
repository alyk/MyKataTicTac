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
import androidx.compose.runtime.getValue
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
import com.example.mykatatictac.types.UiAction
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun createLayout(presenter: Presenter) {
    val context = LocalContext.current
    FlowRow(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        val title by remember { presenter.screenTitle }
        val items = remember { presenter.items }
        val button by remember { presenter.actionButtonText }

        Column(
            Modifier.fillMaxWidth().weight(10f).padding(20.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth().background(Color(0xFFAABBCC)).wrapContentSize().padding(20.dp),
                color = White,
                fontSize = 24.sp
            )
        }
        Column(
            Modifier.fillMaxWidth().weight(12f),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                createCellRow(0, presenter, items)
                createCellRow(1, presenter, items)
                createCellRow(2, presenter, items)
            }
        }
        Column(
            Modifier.fillMaxWidth().weight(10f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth()
            ) {
                FlowRow {
                    Button(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp),
                        onClick = {
                            presenter.sendUserAction(UiAction.NewGame)
                        }
                    ) {
                        Text(button)
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp),
                        onClick = {
                            (context as Activity).finish()
                            presenter.sendUserAction(UiAction.Exit)
                        }
                    ) {
                        Text(
                            text = "Exit"
                        )
                    }
                }
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
