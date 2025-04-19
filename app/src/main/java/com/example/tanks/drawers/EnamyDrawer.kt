package com.example.tanks.drawers

import android.widget.FrameLayout
import com.example.tanks.CELL_SIZE
import com.example.tanks.Unit.drawElement
import com.example.tanks.enums.CELLS_TANKS_SIZE
import com.example.tanks.enums.Direction.DOWN
import com.example.tanks.enums.Material.ENEMY_TANK
import com.example.tanks.models.Coordinate
import com.example.tanks.models.Element
import com.example.tanks.models.Tank


private const val MAX_ENEMY_AMOUNT = 20

class EnamyDrawer(
    private val container: FrameLayout,
    private val elements: MutableList<Element>
) {
    private val respawnList: List<Coordinate>
    private var enemyAmount = 0
    private var currentCoordinate:Coordinate
    private val tanks = mutableListOf<Tank>()

    init {
        respawnList = getRespawnList()
        currentCoordinate = respawnList[0]
    }

    private fun getRespawnList(): List<Coordinate> {
        val respawnList = mutableListOf<Coordinate>()
        respawnList.add(Coordinate(0, 0))
        respawnList.add(
            Coordinate(
                0,
                ((container.width - container.width % CELL_SIZE) / CELL_SIZE -
                        (container.width - container.width % CELL_SIZE) / CELL_SIZE % 2) *
                        CELL_SIZE / 2 - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        respawnList.add(
            Coordinate(
                0,
                (container.width - container.width % CELL_SIZE) - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        return respawnList
    }
    private fun drawView() {
        var index = respawnList.indexOf(currentCoordinate) + 1
        if (index == respawnList.size) {
            index = 0
        }
        currentCoordinate = respawnList[index]
        val enemyTank = Tank(
            Element(
                material = ENEMY_TANK,
                coordinate = currentCoordinate
        ), DOWN,
            BulletDrawer(container)
        )
        enemyTank.element.drawElement(container)
        elements.add(enemyTank.element)
        tanks.add(enemyTank)
    }

    fun moveEnemyTanks() {
        Thread( {
            while (true) {
                removeInconsistentTanks()
                tanks.forEach {
                    it.move(it.direction, container, elements)
                    it.bulletDrawer.makeBulletMove(it, elements)
                }
                Thread.sleep(400)
            }
        }).start()
    }

    fun startEnemyCreation() {
        Thread( {
         while (enemyAmount < MAX_ENEMY_AMOUNT) {
             drawView()
             enemyAmount++
             Thread.sleep(3000)
         }
        }).start()
    }

    private fun removeInconsistentTanks() {
        tanks.removeAll(getIncosistentTanks())
    }

    private fun getIncosistentTanks(): List<Tank> {
        val remavingTanks = mutableListOf<Tank>()
        val allTanksElements = elements.filter { it.material == ENEMY_TANK }
        tanks.forEach {
            if (!allTanksElements.contains(it.element)) {
                remavingTanks.add(it)
            }
        }
        return remavingTanks
    }
}