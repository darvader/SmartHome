package com.darvader.smarthome.ledstrip.christmas

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.ledstrip.LedStrip
import kotlinx.android.synthetic.main.activity_calibrate.*
import java.io.File
import java.util.ArrayList


class Calibrate(val calibrateActivity: CalibrateActivity) {
    private lateinit var imageCapture: ImageCapture

    companion object {
        var currentAddress = LedStrip.currentAddress
        val echoClient = SmartHomeActivity.echoClient
    }

    var isTakingPhoto = false

    data class ChristmasPoint(val p: Point, val color: Color, val lowestDistance: Double) {
        constructor() : this(Point(), Color(), 0.0)
    }

    fun calibrate() {
        val printWriter =
            File(calibrateActivity.applicationContext.filesDir, "ChristmasDots.txt").printWriter()

        for (i in 0..10) {
            val low = (i and 0xff).toByte()
            val high = (i shr 8).toByte()
            val msg = "setPixel=".toByteArray(Charsets.UTF_8) + high + low

            println("setPixel=$high $low")
            echoClient.send(msg, currentAddress)
            Thread.sleep(50)
            findPoints().forEach {
               c -> printWriter.println("$i,${c.p.x},${c.p.y},${c.color.red()},${c.color.green()},${c.color.blue()},${c.lowestDistance}")
            }
        }
        printWriter.close()

        val output =
            File(calibrateActivity.applicationContext.filesDir, "ChristmasDots.txt").bufferedReader().use { it.readText() }
        println(output)

    }

    fun getHighestBlueLocation(image: Bitmap): Point {
        var highestBlue = 0
        val point = Point()
        println("Image size: ${image.width} ${image.height} ")

        for (x in 0 until image.width)
            for (y in 0 until image.height) {
                val color = image.getPixel(x, y)
                val blue = color and 0xff
                if (blue > highestBlue) {
                    highestBlue = blue
                    point.x = x
                    point.y = y
                }
            }
        return point
    }

    private fun getColorDistance(color1: Int, color2: Int): Double {
        val redDistance = Color.red(color1) - Color.red(color2)
        val greenDistance = Color.green(color1) - Color.green(color2)
        val blueDistance = Color.blue(color1) - Color.blue(color2)
        val distance = Math.sqrt(
            (redDistance * redDistance + greenDistance * greenDistance + (blueDistance
                    * blueDistance)).toDouble()
        ) // w ww.j a v  a 2  s  .  c  om
        return distance / 256.0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHighestBlueLocationFast(image: Bitmap): List<ChristmasPoint> {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        println("Image size: $width $height ")
        val cp = ArrayList<ChristmasPoint>()

        for (x in 0 until width)
            for (y in 0 until height) {
                val color = pixels[x + y*width]
                val colorDistance = getColorDistance(Color.BLUE, color)
                if (colorDistance<0.2)
                    cp.add(findInnerPoint(pixels, x, y, width));
            }
        return cp
    }

    private fun findInnerPoint(pixels: IntArray, posx: Int, posy: Int, width: Int):ChristmasPoint {
        val size = 20
        var lowestDistance = Double.MAX_VALUE
        var lastColor = Color()
        val p = Point()
        for (x in posx until posx+size)
            for (y in posy until posy+size) {
                val pos = x + y * width
                if (pos>pixels.size) continue
                val color = pixels[pos]
                val colorDistance = getColorDistance(Color.BLUE, color)

                if (colorDistance < lowestDistance) {
                    lowestDistance = colorDistance
                    lastColor = Color.valueOf(color)
                    p.x = x
                    p.y = y
                    pixels[pos] = 0
                }
            }
        return ChristmasPoint(p, lastColor, lowestDistance)
    }

    private fun findPoints(): List<ChristmasPoint> {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture
        var christPoints = listOf(ChristmasPoint())
        isTakingPhoto = true

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(ContextCompat.getMainExecutor(calibrateActivity), object : ImageCapture.OnImageCapturedCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                val imgProxy = image.image
                var bitmap = imgProxy!!.toBitmap()

                // Rotate the bitmap
                val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()
                if (rotationDegrees != 0f) {
                    val matrix = Matrix()
                    matrix.postRotate(rotationDegrees)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }

                christPoints = getHighestBlueLocationFast(bitmap)
                val canvas = Canvas(bitmap)
                val paint = Paint()
                paint.color = Color.RED
                paint.strokeWidth = 20f
                paint.style = Paint.Style.STROKE

                christPoints.forEach { cp -> canvas.drawCircle(cp.p.x.toFloat(), cp.p.y.toFloat(), 125.0f, paint) }

                calibrateActivity.imageView.setImageBitmap(bitmap)
                image.close()
                isTakingPhoto = false
            }
        })
        while (isTakingPhoto) Thread.sleep(10)

        return christPoints
    }

    fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun setImageCapture(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
    }

}
