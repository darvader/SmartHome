package com.darvader.smarthome.ledstrip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class DrawView : View {
    private var buffer: FloatArray? = null
    var paint = Paint()
    private fun init() {
        paint.color = Color.BLACK
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    var oldX = 0.0f
    var oldY = 0.0f

    public override fun onDraw(canvas: Canvas) {
        canvas.drawLine(0f, 0f, 100f, 100f, paint)
        canvas.drawLine(100f, 0f, 0f, 100f, paint)

        buffer?.forEachIndexed { i, b ->
            canvas.drawLine(oldX, oldY + 500, i.toFloat(), b + 500, paint)
            oldX = i.toFloat()
            oldY = b
        }
    }

    fun setBuffer(buffer: FloatArray) {
        this.buffer = buffer
        invalidate()
    }
}