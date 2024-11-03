package com.example.mykatatictac

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mykatatictac.model.Board.CellState
import com.example.mykatatictac.types.State
import com.example.mykatatictac.types.TurnResult
import com.example.mykatatictac.types.UiAction
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test


class GameFlowTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val uiAction = Channel<UiAction>()
    private val turnAction = Channel<Int>()
    private val stateChangeUpdate = mockk<(State) -> Unit>(relaxed = true)
    private val boardUpdate = mockk<SnapshotStateList<CellState?>>(relaxed = true)
    private val gameResult = mockk<(TurnResult) -> Unit>(relaxed = true)

    @Test
    fun testWinFlow() {
        val flow = initFlow()
        runTest {
            val job = launch { flow.initGameFlow() }

            launch {
                uiAction.send(UiAction.NewGame)
                delay(100)
                turnAction.send(0)
                turnAction.send(3)
                turnAction.send(1)
                turnAction.send(4)
                turnAction.send(2)
                //delay(3000)
                uiAction.send(UiAction.Exit)
            }

            job.join()
            println(" ==================== ")
            advanceUntilIdle()
            coVerify(exactly = 1) { gameResult.invoke(eq(TurnResult.Player1)) }
        }
    }

    private fun initFlow(): GameFlow {
        return GameFlow(
            uiAction,
            turnAction,
            stateChangeUpdate,
            gameResult,
            boardUpdate
        )
    }
}