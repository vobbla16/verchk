package utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class MultipleLineStatus(
    private val data: MutableList<Line>,
    private val eventChannel: Channel<ChangeAction>,
    private val delay: Long = 400
) {
    object SpinnerSymbols {
        private const val FRAME1 = "⣾"
        private const val FRAME2 = "⣷"
        private const val FRAME3 = "⣯"
        private const val FRAME4 = "⣟"
        private const val FRAME5 = "⡿"
        private const val FRAME6 = "⢿"
        private const val FRAME7 = "⣻"
        private const val FRAME8 = "⣽"
        val FRAMES = listOf(FRAME1, FRAME2, FRAME3, FRAME4, FRAME5, FRAME6, FRAME7, FRAME8)
    }

    private val escapeChar = "\u001b"

    private var tick = 0

    private lateinit var renderJob: Job

    suspend fun start() = coroutineScope {
        renderJob = launch {
            try {
                while (true) {
                    renderFrame()
                }
            } catch (e: CancellationException) {
                // coroutine could be cancelled only on delay() so we should shift cursor to write over previous frame
                consoleCursorUpFor(data.size)
                renderFrame()
            }
        }
        eventChannel.receiveAsFlow().onEach { e ->
            when (e) {
                is ChangeAction.ChangeStatus -> {
                    data[e.lineIndex] = data[e.lineIndex].copy(status = e.newStatus)
                }

                is ChangeAction.ChangeText -> {
                    data[e.lineIndex] = data[e.lineIndex].copy(text = e.newText)
                }
            }
        }.collect()
    }

    suspend fun stop() {
        renderJob.cancelAndJoin()
        eventChannel.close()
    }

    private suspend fun renderFrame() {
        data.forEach { line ->
            print("\r")
            when (line.status) {
                is Status.Spinner -> {
                    print(SpinnerSymbols.FRAMES[tick])
                }

                is Status.Success -> {
                    print(line.status.sign)
                }

                is Status.Error -> {
                    print(line.status.sign)
                }
            }
            print(" ")
            println(line.text)
        }
        tick = (tick + 1) % SpinnerSymbols.FRAMES.size
        delay(delay)
        consoleCursorUpFor(data.size)
    }

    private fun consoleCursorUpFor(i: Int) {
        print(escapeChar + "[${i}A")
    }
}

data class Line(
    val status: Status = Status.Spinner,
    val text: String
)

sealed class ChangeAction {
    data class ChangeStatus(val lineIndex: Int, val newStatus: Status) : ChangeAction()
    data class ChangeText(val lineIndex: Int, val newText: String) : ChangeAction()
}

sealed class Status {
    data class Success(val sign: String = "✓") : Status()
    data class Error(val sign: String = "✗") : Status()
    object Spinner : Status()
}