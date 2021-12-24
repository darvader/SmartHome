package com.darvader.smarthome.ledstrip.christmas

import android.graphics.Color
import android.graphics.Point

data class Point3(var x: Float, var y: Float, var z: Float) {
    constructor() : this(0f, 0f, 0f)
    fun size(): Float = x+y+z
}

data class ChristmasPoint(val p: Point, val color: Color, var lowestDistance: Double, var front: Point, var right: Point
                          , var back: Point, var left: Point) {
    var frontLD = 0.0
    var rightLD = 0.0
    var backLD = 0.0
    var leftLD = 0.0
    var index = 0
    var p3 = Point3()

    constructor() : this(Point(), Color(), 0.0, Point(), Point(), Point(), Point())
    constructor(front: Point, right: Point
                , back: Point, left: Point
    ) : this(Point(), Color(), 0.0, front, right, back, left)

    constructor(p: Point, lastColor: Color, lowestDistance: Double) : this(p, lastColor, lowestDistance,
        Point(),
        Point(),
        Point(),
        Point()
    )
}
