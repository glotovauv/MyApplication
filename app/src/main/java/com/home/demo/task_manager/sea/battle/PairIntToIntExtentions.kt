package com.home.demo.task_manager.sea.battle

const val fieldSize = 10

class IntPairRange(val start: Pair<Int, Int>, val end: Pair<Int, Int>) {
    operator fun iterator(): Iterator<Pair<Int, Int>> {
        return object : Iterator<Pair<Int, Int>> {
            private var hasNext: Boolean = start <= end
            private var next = start

            override fun hasNext(): Boolean = hasNext

            override fun next(): Pair<Int, Int> {
                val value = next
                if (value == end) {
                    if (!hasNext) throw kotlin.NoSuchElementException()
                    hasNext = false
                } else {
                    next = next.increment()
                }
                return value
            }
        }
    }
}

operator fun Pair<Int, Int>.rangeTo(end: Pair<Int, Int>): IntPairRange {
    return IntPairRange(this, end)
}

fun Pair<Int, Int>.increment(limit: Int = fieldSize): Pair<Int, Int> {
    val (x, y) = this
    return if (x < limit - 1) x + 1 to y else 0 to y + 1
}

operator fun Pair<Int, Int>.compareTo(other: Pair<Int, Int>): Int {
    val (x1, y1) = this
    val (x2, y2) = other
    return when {
        x1 == x2 && y1 == y2 -> 0
        y1 < y2 || (y1 == y2 && x1 < x2) -> -1
        else -> 1
    }
}
