package com.home.demo.task_manager.test

class Field {
    private val cells: Array<Array<Int>> = Array(fieldSize) { Array(fieldSize) { 0 } }
    private val cellToShip: MutableMap<Pair<Int, Int>, Ship> = HashMap()
    private val shipList: MutableList<Ship> = ArrayList()

    fun addShip(ship: Ship) {
        shipList.add(ship)
        val (x, y) = ship.startPosition
        val shift = { value: Int -> if (ship.isVertical) x to y + value else x + value to y }
        for (value in 0 until ship.size) {
            cellToShip[shift(value)] = ship
        }
        markCells(ship)
    }

    fun findRandomPlace(ship: Ship) {
        ship.isVertical = getRandomOrientation()
        val startPosition = getRandomStartPosition(ship)
        val limitX = if (ship.isVertical) fieldSize else fieldSize - ship.size
        val limitY = if (ship.isVertical) fieldSize - ship.size else fieldSize

        var current = startPosition
        while (!isFree(current, ship.size, ship.isVertical)) {
            current = current.increment(limitX)
            if (current.second == limitY) {
                current = 0 to 0
            }
            if (startPosition == current) break
        }
        ship.startPosition = current
        addShip(ship)
    }

    fun shoot(cell: Pair<Int, Int>): Response {
        if (!inRange(cell)) {
            return Response.ERROR_SHOOT
        }
        if (cells[cell.first][cell.second] > 0) {
            return Response.DOUBLE_SHOOT
        }
        cells[cell.first][cell.second] = 1

        val ship: Ship? = cellToShip[cell]
        ship?.shoot(cell)
        return when {
            ship == null -> Response.AWAY
            ship.isAlive -> Response.HURT
            else -> Response.KILLED
        }
    }

    private fun getRandomOrientation(): Boolean {
        val isVertical = (0..1).random()
        return isVertical == 1
    }

    private fun getRandomStartPosition(ship: Ship): Pair<Int, Int> {
        val x = (0 until fieldSize).random()
        val y = (0 until fieldSize - ship.size).random()
        return if (ship.isVertical) x to y else y to x
    }

    private fun markCells(ship: Ship) {
        val lastFieldIndex = fieldSize - 1
        val (x, y) = ship.startPosition
        val (endX, endY) = ship.getEndPosition()

        val start = (x - 1).coerceIn(0, lastFieldIndex) to (y - 1).coerceIn(0, lastFieldIndex)
        val end = (endX + 1).coerceIn(0, lastFieldIndex) to (endY + 1).coerceIn(0, lastFieldIndex)

        for (point in start..end) {
            cells[point.first][point.second] = -1
        }
    }

    private fun isFree(pos: Pair<Int, Int>, size: Int, isVertical: Boolean): Boolean {
        var (x, y) = pos
        for (i in 0 until size) {
            if (cells[x][y] == -1) return false
            if (isVertical) y++
            else x++
        }
        return true
    }


    private fun inRange(cell: Pair<Int, Int>) = inRange(cell.first) && inRange(cell.second)
    private fun inRange(pos: Int) = pos in 0 until fieldSize

    enum class Response {
        AWAY, HURT, KILLED, DOUBLE_SHOOT, ERROR_SHOOT
    }
}
