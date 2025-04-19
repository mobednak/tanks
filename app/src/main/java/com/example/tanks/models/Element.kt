package com.example.tanks.models

import android.view.View
import com.example.tanks.enums.Material

data class Element constructor(
    val viewId:Int = View.generateViewId(),
    val material: Material,
    var coordinate: Coordinate,
    val width: Int = material.width,
    val height: Int = material.height
)
