package com.home.demo.task_manager.sea.battle.model

import android.graphics.RectF
import android.util.Log
import com.home.demo.task_manager.sea.battle.compareTo
import com.home.demo.task_manager.sea.battle.fieldSize
import java.util.*
import kotlin.collections.ArrayList

class TempField(val start: Int, val delta: Int) {
    var totalCount = 0
    val shipCount = Array(4) { 0 }
    private val maxSize = 4

    var currentNumber = -1
    val stop = start + delta * fieldSize

    var currentRect: RectF? = null
    var currentShip: Ship? = null
    var isVertical = false
    var isCorrectLocation = true

    val shipList: MutableList<Pair<Ship, RectF>> = ArrayList()
    val cellToShip: MutableMap<Pair<Int, Int>, Int> = TreeMap(Pair<Int, Int>::compareTo)

    fun insideBorders(rect: RectF, offsetX: Int = 0, offsetY: Int = 0): Boolean {
        return rect.left + offsetX >= start && rect.top + offsetY >= start
                && rect.right + offsetX <= stop && rect.bottom + offsetY <= stop
    }

    fun isAvailableToAdd(size: Int): Boolean {
        return size in 1..maxSize && shipCount[size - 1] <= maxSize - size
    }

    fun getShipPositionIfExist(x: Int, y: Int): Pair<Int, Int>? {
        val cell = findTopOfCell(x, y)
        if (isCurrentRect(x, y)) return cell
        return cellToShip[cell]?.let { cell }
    }

    fun getShipNumberIfExist(x: Int, y: Int): Int? {
        if (isCurrentRect(x, y)) return currentNumber
        return cellToShip[x to y]
    }

    fun isCurrentRect(x: Int, y: Int): Boolean {
        return currentRect?.let {
            return it.left <= x && x < it.right
                    && it.top <= y && y < it.bottom
        } ?: false
    }

    fun findTopOfCell(x: Int, y: Int): Pair<Int, Int> = round(x) to round(y)
    private fun round(x: Int): Int = ((x - start) / delta) * delta + start

    fun getPoint(x: Int, y: Int, pos: Int, isVertical: Boolean): Pair<Int, Int> {
        return if (isVertical) x to y + pos * delta else x + pos * delta to y
    }

    fun isPositionCorrect(rect: RectF?): Boolean {
        // Log.i("isPositionCorrect", rect.toString())
        if (rect == null) return false
        val startX = (rect.left - delta).toInt().coerceAtLeast(start)
        val startY = (rect.top - delta).toInt().coerceAtLeast(start)
        val endX = rect.right.toInt().coerceAtMost(stop)
        val endY = rect.bottom.toInt().coerceAtMost(stop)
        for (x in startX..endX step delta) {
            for (y in startY..endY step delta) {
                // Log.i("check (x, y)", (x to y).toString())
                val number = cellToShip[x to y]
                if (number != null && number != currentNumber) {
                    Log.i("number=", number.toString())
                    return false
                }
            }
        }
        return true
    }
}