package utils

class ConsoleTableBuilder {
    private val _headers = mutableListOf<String>()
    private val _lines = mutableListOf<MutableList<String>>()
    private var width: Int = 0
    private var height = 0

    companion object {
        const val TOP_LEFT_CORNER = "╭"
        const val TOP_RIGHT_CORNER = "╮"
        const val BOTTOM_LEFT_CORNER = "╰"
        const val BOTTOM_RIGHT_CORNER = "╯"
        const val VERTICAL_SPLITTER = "┃"
        const val HORIZONTAL_SPLITTER = "━"
        const val TOP_TEE = "┷"
        const val RIGHT_TEE = "┣"
        const val BOTTOM_TEE = "┯"
        const val LEFT_TEE = "┫"
        const val FOUR_WAY = "╋"
    }

    fun headers(vararg headers: String) {
        this._headers.addAll(headers)
    }

    fun addRow(vararg columns: String) {
        if (_lines.size == 0) {
            width = columns.size
        } else {
            if (columns.size != width) {
                throw Exception("Incorrect count of columns")
            }
        }
        height++
        this._lines.add(columns.toMutableList())
    }

    fun build(): String {
        var out = ""

        val columnsWidth: MutableList<Int> = _headers.map { it.length }.toMutableList()
        _lines.forEach { line ->
            line.forEachIndexed { i, s ->
                if (s.length > columnsWidth[i]) columnsWidth[i] = s.length
            }
        }

        out += drawLineWithIntervals(TOP_LEFT_CORNER, HORIZONTAL_SPLITTER, TOP_RIGHT_CORNER, BOTTOM_TEE, columnsWidth)
        out += "\n"

        out += drawContentLine(VERTICAL_SPLITTER, _headers, columnsWidth)
        out += "\n"

        out += drawLineWithIntervals(RIGHT_TEE, HORIZONTAL_SPLITTER, LEFT_TEE, FOUR_WAY, columnsWidth)
        out += "\n"

        _lines.forEach { line ->
            out += drawContentLine(VERTICAL_SPLITTER, line, columnsWidth)
            out += "\n"
        }

        out += drawLineWithIntervals(
            BOTTOM_LEFT_CORNER,
            HORIZONTAL_SPLITTER,
            BOTTOM_RIGHT_CORNER,
            TOP_TEE,
            columnsWidth
        )
        out += "\n"

        return out
    }

    private fun drawContentLine(
        splitter: String,
        content: List<String>,
        intervals: List<Int>
    ): String {
        var out = ""
        out += splitter
        content.forEachIndexed { i, c ->
            out += c
            out += " ".repeat(intervals[i] - c.length)
            out += splitter
        }

        return out
    }

    private fun drawLineWithIntervals(
        leftBorderSymbol: String,
        mainSymbol: String,
        rightBorderSymbol: String,
        intervalSymbol: String,
        intervalWidths: List<Int>
    ): String {
        var out = ""
        out += leftBorderSymbol
        intervalWidths.dropLast(1).forEach {
            out += mainSymbol.repeat(it)
            out += intervalSymbol
        }
        out += mainSymbol.repeat(intervalWidths.last())
        out += rightBorderSymbol

        return out
    }
}

fun consoleTable(builder: ConsoleTableBuilder.() -> Unit): ConsoleTableBuilder = ConsoleTableBuilder().apply(builder)