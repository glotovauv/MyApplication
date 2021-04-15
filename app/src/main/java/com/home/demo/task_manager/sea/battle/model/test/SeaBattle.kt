package com.home.demo.task_manager.sea.battle.model.test

import com.home.demo.task_manager.sea.battle.model.Ship

class SeaBattle {



    fun createRandomField() {
        val computerField = Field()
        val shipList = createEmptyShips()
        for (ship in shipList) {
            computerField.findRandomPlace(ship)
        }
    }

    private fun createEmptyShips(): List<Ship> {
        val ships: MutableList<Ship> = ArrayList()
        val maxCount = 4
        for (size in 4 downTo 1) {
            for (i in 0..maxCount - size) {
                ships.add(Ship(size))
            }
        }
        return ships
    }
}