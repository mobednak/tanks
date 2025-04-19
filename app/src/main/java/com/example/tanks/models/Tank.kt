package com.example.tanks.models

import android.view.View
import android.widget.FrameLayout
import com.example.tanks.CELL_SIZE
import com.example.tanks.Unit.checkViewCanMoveThrounghBorder
import com.example.tanks.Unit.getElementByCoordinates
import com.example.tanks.Unit.runOnUiThread
import com.example.tanks.binding
import com.example.tanks.drawers.BulletDrawer
import com.example.tanks.enums.Direction
import com.example.tanks.enums.Direction.DOWN
import com.example.tanks.enums.Direction.LEFT
import com.example.tanks.enums.Direction.RIGHT
import com.example.tanks.enums.Direction.UP
import com.example.tanks.enums.Material
import kotlin.random.Random


class Tank(
    val element: Element,
    var direction: Direction,
    val bulletDrawer: BulletDrawer
) {
    fun move(
        direction: Direction,
        container: FrameLayout,
        elementsOnCoordinate: List<Element>) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val currentCoordinate = getTankCurretCoordinate(view)
        this.direction = direction
        view.rotation = direction.rotation
        val nextCoordinate = getTankNextCoordinate(view)
        if (view.checkViewCanMoveThrounghBorder(
                nextCoordinate
            ) && element.checkTankCanMoveThrounghMaterial(nextCoordinate, elementsOnCoordinate)
        ) {
            emulateViewMoving(container, view)
            element.coordinate = nextCoordinate
        } else {
            element.coordinate = currentCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
            changDirectionForEnemyTank()
        }
    }

    private fun changDirectionForEnemyTank() {
        if (element.material == Material.ENEMY_TANK) {
            val randomDirection = Direction.entries[Random.nextInt(Direction.entries.size)]
            this.direction = randomDirection
        }
    }

    private fun emulateViewMoving(container: FrameLayout, view: View){
        container.runOnUiThread {
            binding.container.removeView(view)
            binding.container.addView(view, 0)
        }
    }

    private fun getTankCurretCoordinate(tank: View): Coordinate {
        return Coordinate(
            (tank.layoutParams as FrameLayout.LayoutParams).topMargin,
            (tank.layoutParams as FrameLayout.LayoutParams).leftMargin
        )
    }

    private fun  getTankNextCoordinate(view: View): Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        when(direction) {
            UP ->{
                view.rotation = 0f
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }

            DOWN ->{
                view.rotation = 180f
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            LEFT ->{
                view.rotation = 270f
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

            RIGHT ->{
                view.rotation = 90f
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
        }
        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }

    private fun Element.checkTankCanMoveThrounghMaterial(
        coordinate: Coordinate,
        elementsOnCoordinate: List<Element>
    ): Boolean {
       for (anyCoordinate in getTankCoordinates(coordinate)) {
            val element = getElementByCoordinates(anyCoordinate, elementsOnCoordinate)
            if (element != null && !element.material.tankConGoThrough) {
                if (this == element) {
                    continue
                } else {
                    return false
                }
            }
        }
        return true
    }

    private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left))
        coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE))
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left + CELL_SIZE))
        return coordinateList
    }
}