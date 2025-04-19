package com.example.tanks.Unit

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tanks.CELL_SIZE
import com.example.tanks.binding
import com.example.tanks.models.Coordinate
import com.example.tanks.models.Element

fun View.checkViewCanMoveThrounghBorder(coordinate: Coordinate): Boolean {
    return (coordinate.top >= 0 &&
        coordinate.top + this.height <= binding.container.height &&
        coordinate.left >= 0 &&
        coordinate.left + this.width <= binding.container.width)
}

fun getElementByCoordinates(
    coordinate: Coordinate,
    elementsOnCoordinate: List<Element>
): Element? {
    for (element in elementsOnCoordinate) {
        for (height in 0 until element.height) {
            for (width in 0 until element.width) {
                val searchCoordinate = Coordinate(
                    top = element.coordinate.top + height * CELL_SIZE,
                    left = element.coordinate.left + width * CELL_SIZE
                )
                if (coordinate == searchCoordinate) {
                    return element
                }
            }
        }
    }
    return null
}

fun Element.drawElement(container: FrameLayout) {
    val view = ImageView(container.context)
    val layoutParams = FrameLayout.LayoutParams(
        this.material.width * CELL_SIZE,
        this.material.height * CELL_SIZE)
    this.material.image?.let { view.setImageResource(it) }
    layoutParams.topMargin = coordinate.top
    layoutParams.leftMargin = coordinate.left
    view.id = this.viewId
    view.layoutParams = layoutParams
    view.scaleType = ImageView.ScaleType.FIT_XY
    container.runOnUiThread {
        container.addView(view)
    }
}

fun FrameLayout.runOnUiThread(block: () -> Unit) {
    (this.context as Activity).runOnUiThread {
        block()
    }
}