package utils

import kotlinx.coroutines.*

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

suspend fun consoleSpinner(additionalText: String, delayMs: Long = 400L) {
    try {
        while (true) {
            for (frame in SpinnerSymbols.FRAMES) {
                print("\r${frame} $additionalText")
                delay(delayMs)
            }
        }
    } catch (e: CancellationException) {
        print("\r")
    }
}