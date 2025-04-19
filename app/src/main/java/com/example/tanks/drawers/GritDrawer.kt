package com.example.tanks.drawers

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import com.example.tanks.CELL_SIZE


class GritDrawer(private val container: FrameLayout?) {
    private val allLinea = mutableListOf<View>()

    fun remaveGrit() {
        allLinea.forEach {
            container?.removeView(it)
        }
    }
    fun drawGrit() {
        drawHorizontalLines(container)
        drawVerticalLines(container)
    }

    private fun drawHorizontalLines(container: FrameLayout?) {
        var topMargin = 0
        while (topMargin <= container!!.height) {
            val horizontalLine = View(container.context)
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1)
            topMargin += CELL_SIZE
            layoutParams.topMargin = topMargin
            horizontalLine.layoutParams = layoutParams
            horizontalLine.setBackgroundColor(Color.WHITE)
            allLinea.add(horizontalLine)
            container.addView(horizontalLine)
        }
    }

    private fun drawVerticalLines(container: FrameLayout?) {
        var leftMargin = 0
        while (leftMargin <= container!!.width) {
            val verticalLine = View(container.context)
            val layoutParams = FrameLayout.LayoutParams(1, FrameLayout.LayoutParams.MATCH_PARENT)
            leftMargin += CELL_SIZE
            layoutParams.leftMargin = leftMargin
            verticalLine.layoutParams = layoutParams
            verticalLine.setBackgroundColor(Color.WHITE)
            allLinea.add(verticalLine)
            container.addView(verticalLine)
        }
    }
}