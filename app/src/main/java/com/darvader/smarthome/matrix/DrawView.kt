package com.darvader.smarthome.matrix

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.set
import com.darvader.smarthome.matrix.activity.LedMatrixActivity


class DrawView : View {
    private var buffer: FloatArray? = null
    var paint = Paint()
    var ledMatrix: LedMatrix? = null
    private fun init() {
        paint.color = Color.BLACK
        ledMatrix = LedMatrixActivity.ledMatrix
        ledMatrix?.pictureMode()
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

    private val maxX = 64
    private val maxY = 32

    var zoomMandelbrot = 150.0f/10
    val MAX_ITER = 255 * 2

    fun mandelbrot(bitmap: Bitmap) {
        zoomMandelbrot *= 1.01f;
        for (y in 0 until maxY) {
            for (x in 0 until maxX) {
                var zx = 0.0
                var zy = 0.0
                val cX = (x - maxX /2-11 ) / zoomMandelbrot
                val cY = (y - maxY /2-2 ) / zoomMandelbrot
                var iter = MAX_ITER
                while (zx * zx + zy * zy < 4.0 && iter > 0) {
                    val tmp = zx * zx - zy * zy + cX
                    zy = 2.0 * zx * zy + cY
                    zx = tmp
                    iter--
                }
                bitmap.set(x, y, iter shl 8 )
            }
        }
    }


    var oldX = 0.0f
    var oldY = 0.0f

    val bitmap = Bitmap.createBitmap(maxX, maxY, Bitmap.Config.RGB_565)

    public override fun onDraw(canvas: Canvas) {
        mandelbrot(bitmap)
        canvas.drawBitmap(bitmap, null, Rect(0,0,640, 320), paint)
        ledMatrix?.sendBitmap(bitmap)

    }
}