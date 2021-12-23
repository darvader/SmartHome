package com.darvader.smarthome.ledstrip.christmas

import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF

data class ChristmasPoint(val p: Point, val color: Color, val lowestDistance: Double, var front: Point, var right: Point
                          , var back: Point, var left: Point
) {
    var index = 0
    var pointF = PointF()

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
