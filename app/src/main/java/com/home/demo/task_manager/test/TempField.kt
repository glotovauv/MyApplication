package com.home.demo.task_manager.test

import android.graphics.RectF
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class TempField(val start: Int, val delta: Int) {
    private var totalCount = 0
    private val shipCount = Array(4) { 0 }
    private val maxSize = 4

    var currentNumber = -1
        private set
    private val end = start + delta * fieldSize

    var currentRect: RectF? = null
    var currentShip: Ship? = null
    var isVertical = false


    val shipList: MutableList<Pair<Ship, RectF>> = ArrayList()
    private val cellToShip: MutableMap<Pair<Int, Int>, Int> = TreeMap(Pair<Int, Int>::compareTo)

    fun addShip(ship: Ship, rect: RectF) {
        currentNumber = -1
        currentRect = rect
        currentShip = ship
        isVertical = ship.isVertical
    }

    fun clearField() {
        totalCount = 0
        for (i in shipCount.indices) {
            shipCount[i] = 0
        }
        shipList.clear()
        cellToShip.clear()
        resetShip()
    }

    fun isAvailableToAdd(size: Int): Boolean {
        return size in 1..maxSize && shipCount[size - 1] <= maxSize - size
    }

    fun saveAndReset() {
        if (!isPositionCorrect(currentRect)) {
            if (currentNumber != -1) resetSettings(currentRect, currentShip)
            resetShip()
            return
        }
        savePosition()
        resetShip()
    }

    fun savePosition() {
        if (currentNumber == -1) {
            saveNewShip(currentShip, currentRect)
        } else {
            changeSavedShip(currentShip, currentRect, currentNumber)
        }
        Log.i("cellToShip", cellToShip.toString())
    }

    fun selectShip(x: Int, y: Int) {
        //  Log.i("selected", "$x, $y")
        val cell = findTopOfCell(x, y)
        Log.i("rounded", cell.toString())
        if (isCurrentRect(currentRect, x, y)) return

        val number = cellToShip[cell]
        Log.i("number=", number.toString())
        number?.let {
            if (currentNumber != number) {
                saveAndReset()
                currentNumber = number
                currentShip = shipList[number].first
                currentRect = shipList[number].second
                currentShip?.let { isVertical = it.isVertical }
                Log.i("selected rect", currentRect.toString())
            }
        }
        if (number == null) {
            saveAndReset()
        }
    }

    fun getShipPositionIfExist(x: Int, y: Int): Pair<Int, Int>? {
        val cell = findTopOfCell(x, y)
        if (isCurrentRect(currentRect, x, y)) return cell
        return cellToShip[cell]?.let { cell }
    }

    private fun isCurrentRect(rect: RectF?, x: Int, y: Int): Boolean {
        if (rect != null) {
            return rect.left <= x && x < rect.right
                    && rect.top <= y && y < rect.bottom
        }
        return false
    }

    fun findTopOfCell(x: Int, y: Int): Pair<Int, Int> = round(x) to round(y)
    private fun round(x: Int): Int = ((x - start) / delta) * delta + start

    private fun saveNewShip(ship: Ship?, rect: RectF?) {
        if (ship != null && rect != null) {
            copySettings(ship, rect)
            shipList.add(Pair(ship, rect))
            for (i in 0 until ship.size) {
                cellToShip[getPoint(ship.getX(), ship.getY(), i, ship.isVertical)] = totalCount
            }
            currentNumber = totalCount
            shipCount[ship.size - 1]++
            totalCount++
        }
    }

    private fun resetShip() {
        currentNumber = -1
        currentShip = null
        currentRect = null
    }

    private fun changeSavedShip(ship: Ship?, rect: RectF?, number: Int) {
        if (ship != null && rect != null) {
            for (i in 0 until ship.size) {
                cellToShip.remove(getPoint(ship.getX(), ship.getY(), i, ship.isVertical))
            }
            copySettings(ship, rect)
            for (i in 0 until ship.size) {
                cellToShip[getPoint(ship.getX(), ship.getY(), i, ship.isVertical)] = number
            }
        }
    }

    private fun getPoint(x: Int, y: Int, pos: Int, isVertical: Boolean): Pair<Int, Int> {
        return if (isVertical) x to y + pos * delta else x + pos * delta to y
    }

    private fun copySettings(ship: Ship, rect: RectF) {
        ship.startPosition = rect.left.toInt() to rect.top.toInt()
        ship.isVertical = isVertical
        Log.i("copy settings", ship.toString())
    }

    private fun resetSettings(rect: RectF?, ship: Ship?) {
        if (rect == null || ship == null) return
        rect.left = ship.getX().toFloat()
        rect.top = ship.getY().toFloat()
        if (ship.isVertical) {
            rect.right = rect.left + delta
            rect.bottom = rect.top + ship.size * delta
        } else {
            rect.right = rect.left + ship.size * delta
            rect.bottom = rect.top + delta
        }
        Log.i("resetSettings", rect.toString())
    }

    fun isPositionCorrect(rect: RectF?): Boolean {
        // Log.i("isPositionCorrect", rect.toString())
        if (rect == null) return false
        val startX = (rect.left - delta).toInt().coerceAtLeast(start)
        val startY = (rect.top - delta).toInt().coerceAtLeast(start)
        val endX = rect.right.toInt().coerceAtMost(end)
        val endY = rect.bottom.toInt().coerceAtMost(end)
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