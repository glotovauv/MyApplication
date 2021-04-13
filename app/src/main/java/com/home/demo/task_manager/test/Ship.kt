package com.home.demo.task_manager.test

class Ship(val size: Int) {
    var isVertical = false
    var startPosition: Pair<Int, Int> = Pair(0, 0)
    var isAlive: Boolean = true
    private set

    private var injures: MutableList<Pair<Int, Int>> = ArrayList()

    fun shoot(cell: Pair<Int, Int>) {
        injures.add(cell)
        if (injures.size == size) {
            isAlive = false
        }
    }

    fun getEndPosition(): Pair<Int, Int> {
        val (x, y) = startPosition
        return if (isVertical) x to y + size - 1 else x + size - 1 to y
    }

    fun getX(): Int {
        return startPosition.first
    }

    fun getY(): Int {
        return startPosition.second
    }

    override fun toString(): String {
        return "Ship(size=$size, isVertical=$isVertical, startPosition=$startPosition)"
    }


}