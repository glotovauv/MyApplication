package com.home.demo.task_manager.sea.battle.service

import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.transform
import com.home.demo.task_manager.sea.battle.model.Ship
import com.home.demo.task_manager.sea.battle.model.TempField

class CommandService(private val field: TempField) {
    private val transformMatrix = Matrix()

    fun addShip(size: Int): Boolean {
        saveAndReset()
        if (field.isAvailableToAdd(size)) {
            val ship = Ship(size)
            val (x, y) = (ship.getX() + field.start).toFloat() to (ship.getY() + field.start).toFloat()
            val rect = RectF(x, y, field.delta * ship.size + x, field.delta + y)
            field.currentNumber = -1
            field.currentRect = rect
            field.currentShip = ship
            field.isVertical = ship.isVertical
            checkShipLocation()
            return true
        }
        return false
    }

    fun move(dx: Int, dy: Int): Boolean {
        field.currentRect?.apply {
            val offsetX = dx * field.delta
            val offsetY = dy * field.delta
            if (field.insideBorders(this, offsetX, offsetY)) {
                offset(offsetX.toFloat(), offsetY.toFloat())
                if (field.isPositionCorrect(this)) {
                    savePosition()
                    field.isCorrectLocation = true
                } else {
                    field.isCorrectLocation = false
                }
                return true
            }
        }
        return false
    }

    fun rotate(): Boolean {
        field.currentRect?.apply {
            transformMatrix.reset()
            if (field.isVertical) {
                transformMatrix.setRotate(-90F, left, top)
                transformMatrix.postTranslate(0F, field.delta.toFloat())
            } else {
                transformMatrix.setRotate(90F, left, top)
                transformMatrix.postTranslate(field.delta.toFloat(), 0F)
            }
            transform(transformMatrix)
            field.isVertical = !field.isVertical
            if (right > field.stop) {
                offset(field.stop - right, 0F)
            } else if (bottom > field.stop) {
                offset(0F, field.stop - bottom)
            }
            checkShipLocation()
            return true
        }
        return false
    }

    fun clearField() {
        field.totalCount = 0
        for (i in field.shipCount.indices) {
            field.shipCount[i] = 0
        }
        field.shipList.clear()
        field.cellToShip.clear()
        resetShip()
    }

    fun selectShip(x: Int, y: Int) {
        //  Log.i("selected", "$x, $y")
        val cell = field.findTopOfCell(x, y)
        Log.i("rounded", cell.toString())
        if (field.isCurrentRect(x, y)) return

        val number = field.cellToShip[cell]
        Log.i("number=", number.toString())
        number?.let {
            if (field.currentNumber != number) {
                saveAndReset()
                field.currentNumber = number
                field.currentShip = field.shipList[number].first
                field.currentRect = field.shipList[number].second
                field.currentShip?.let { field.isVertical = it.isVertical }
                Log.i("selected rect", field.currentRect.toString())
            }
        }
        if (number == null) {
            saveAndReset()
        }
    }

    private fun resetShip() {
        field.currentNumber = -1
        field.currentShip = null
        field.currentRect = null
    }

    private fun saveAndReset() {
        if (!field.isPositionCorrect(field.currentRect)) {
            if (field.currentNumber != -1) resetSettings(field.currentRect, field.currentShip)
            resetShip()
            return
        }
        savePosition()
        resetShip()
    }



    private fun savePosition() {
        if (field.currentNumber == -1) {
            saveNewShip(field.currentShip, field.currentRect)
        } else {
            changeSavedShip(field.currentShip, field.currentRect, field.currentNumber)
        }
        Log.i("cellToShip", field.cellToShip.toString())
    }

    private fun checkShipLocation() {
        field.currentRect?.let {
            field.isCorrectLocation = field.insideBorders(it) && field.isPositionCorrect(it)
        }
    }

    private fun saveNewShip(ship: Ship?, rect: RectF?) {
        if (ship != null && rect != null) {
            copySettings(ship, rect)
            field.shipList.add(Pair(ship, rect))
            for (i in 0 until ship.size) {
                field.cellToShip[field.getPoint(ship.getX(), ship.getY(), i, ship.isVertical)] = field.totalCount
            }
            field.currentNumber = field.totalCount
            field.shipCount[ship.size - 1]++
            field.totalCount++
        }
    }

    private fun changeSavedShip(ship: Ship?, rect: RectF?, number: Int) {
        if (ship != null && rect != null) {
            for (i in 0 until ship.size) {
                field.cellToShip.remove(field.getPoint(ship.getX(), ship.getY(), i, ship.isVertical))
            }
            copySettings(ship, rect)
            for (i in 0 until ship.size) {
                field.cellToShip[field.getPoint(ship.getX(), ship.getY(), i, ship.isVertical)] = number
            }
        }
    }

    private fun copySettings(ship: Ship, rect: RectF) {
        ship.startPosition = rect.left.toInt() to rect.top.toInt()
        ship.isVertical = field.isVertical
        Log.i("copy settings", ship.toString())
    }

    private fun resetSettings(rect: RectF?, ship: Ship?) {
        if (rect == null || ship == null) return
        rect.left = ship.getX().toFloat()
        rect.top = ship.getY().toFloat()
        if (ship.isVertical) {
            rect.right = rect.left + field.delta
            rect.bottom = rect.top + ship.size * field.delta
        } else {
            rect.right = rect.left + ship.size * field.delta
            rect.bottom = rect.top + field.delta
        }
        Log.i("resetSettings", rect.toString())
    }
}