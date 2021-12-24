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
import kotlin.math.max


class Calibrate(val calibrateActivity: CalibrateActivity) {
    private lateinit var imageCapture: ImageCapture

    companion object {
        var currentAddress = LedStrip.currentAddress
        val echoClient = SmartHomeActivity.echoClient
    }

    var isTakingPhoto = false

    fun calibrate() {
        val currentTimeMillis = System.currentTimeMillis()
        val fileName = "ChristmasDots$currentTimeMillis.txt"
        val printWriter =
            File(calibrateActivity.applicationContext.filesDir, fileName).printWriter()

        for (i in 0..499) {
            val low = (i and 0xff).toByte()
            val high = (i shr 8).toByte()
            val msg = "setPixel=".toByteArray(Charsets.UTF_8) + high + low

            println("setPixel=$high $low")
            echoClient.send(msg, currentAddress)
            Thread.sleep(50)
            val c = findPoint()
            printWriter.println("$i,${c.p.x},${c.p.y},${c.color.red()},${c.color.green()},${c.color.blue()},${c.lowestDistance}")
        }
        printWriter.close()

        val output =
            File(calibrateActivity.applicationContext.filesDir, fileName).bufferedReader().use { it.readText() }
        println(output)

    }

    fun collectPoints() {
        val files = calibrateActivity.applicationContext.filesDir.listFiles()
        files.forEach {
            f -> println("File: $f")
        }
        var filesList = files.filter { f -> f.name.startsWith("ChristmasDots") }.sortedBy { f -> f.name.split("ChristmasDots")[1] }
        val front = filesList[0]
        val right = filesList[1]
        val back = filesList[2]
        val left = filesList[3]

        val christmasPoints = ArrayList<ChristmasPoint>(500)
        for (i in 0..499) { christmasPoints.add(ChristmasPoint())}

        front.bufferedReader().use { it.lines().forEach { l ->
                val split = l.split(",")
                val index = split[0].toInt()
                val p = Point(split[1].toInt(), split[2].toInt())
                val cp = ChristmasPoint()
                cp.index = index
                cp.front = p
                cp.frontLD = split[6].toDouble()
                christmasPoints[index] = cp
        }}
        right.bufferedReader().use { it.lines().forEach { l ->
                val split = l.split(",")
                val index = split[0].toInt()
                val p = Point(split[1].toInt(), split[2].toInt())
                christmasPoints[index].rightLD = split[6].toDouble()
                christmasPoints[index].right = p
            }}
        back.bufferedReader().use { it.lines().forEach { l ->
                val split = l.split(",")
                val index = split[0].toInt()
                val p = Point(split[1].toInt(), split[2].toInt())
                christmasPoints[index].backLD = split[6].toDouble()
                christmasPoints[index].back = p
            }}
        left.bufferedReader().use { it.lines().forEach { l ->
                val split = l.split(",")
                val index = split[0].toInt()
                val p = Point(split[1].toInt(), split[2].toInt())
                christmasPoints[index].leftLD = split[6].toDouble()
                christmasPoints[index].left = p
            }}

        var count = 0
        christmasPoints.forEach { p ->
            var i = 0
            if (p.front.x>0 && (p.right.x>0 || p.left.x>0)) i+=1
            if (p.back.x>0 && (p.right.x>0 || p.left.x>0)) i+=1

            val message = "i: ${p.index}, front: ${p.front}, right: ${p.right}, back: ${p.back}, left: ${p.left}"
            if (i<1) {
                System.err.println(message)
                count++
            }
            else {
               // println(message)
            }
        }
        println("Count: $count")
        calculate3dPoints(christmasPoints)


    }

    val maxWidth = 2448
    val maxHeight = 3264

    private fun calculate3dPoints(christmasPoints: ArrayList<ChristmasPoint>) {
        christmasPoints.filter{ it.index < 500}.forEach { p ->
            if (p.frontLD < p.backLD && p.front.x>0) {
                if (p.rightLD < p.leftLD && p.right.x>0) {
                    p.p3.x = normalize(p.front.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.front.y.toFloat(), maxHeight)
                    p.p3.z = normalize(p.right.x.toFloat(), maxWidth)
                } else
                if (p.left.x>0) {
                    p.p3.x = normalize(p.front.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.front.y.toFloat(), maxHeight)
                    p.p3.z = -normalize(p.left.x.toFloat(), maxWidth)
                } else {
                    p.p3.x = normalize(p.front.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.front.y.toFloat(), maxHeight)
                    p.p3.z = 0f
                }
            } else
            if (p.back.x>0) {
                if (p.rightLD < p.leftLD && p.right.x>0) {
                    p.p3.x = -normalize(p.back.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.back.y.toFloat(), maxHeight)
                    p.p3.z = normalize(p.right.x.toFloat(), maxWidth)
                } else
                if (p.left.x>0) {
                    p.p3.x = -normalize(p.back.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.back.y.toFloat(), maxHeight)
                    p.p3.z = -normalize(p.left.x.toFloat(), maxWidth)
                } else {
                    p.p3.x = -normalize(p.back.x.toFloat(), maxWidth)
                    p.p3.y = normalize(p.back.y.toFloat(), maxHeight)
                    p.p3.z = 0f
                }
            } else if (p.left.x > 0) {
                p.p3.x = 0f
                p.p3.y = normalize(p.left.y.toFloat(), maxHeight)
                p.p3.z = -normalize(p.left.x.toFloat(), maxWidth)
            } else if (p.right.x > 0) {
                p.p3.x = 0f
                p.p3.y = normalize(p.right.y.toFloat(), maxHeight)
                p.p3.z = normalize(p.right.x.toFloat(), maxWidth)
            }

        }
        christmasPoints.forEach { p ->
            println("3D Id: ${p.index} P:${p.p3}")
        }

        val printWriter =
            File(calibrateActivity.applicationContext.filesDir, "3dPoints.txt").printWriter()
        christmasPoints.forEach {
            with(it) {
                printWriter.println("$index,${p3.x},${p3.y},${p3.z}")
            }
        }
        printWriter.close()
        val maxOf = christmasPoints.maxByOrNull { p -> p.p3.y }
        println("Max: ${maxOf?.index}, ${maxOf?.p3}")

        var first = true
        var lineCoords = ArrayList<Float>()
        christmasPoints.forEach { p ->
            println("3d: ${p.p3}")
            lineCoords.add(p.p3.x)
            lineCoords.add(p.p3.y)
            lineCoords.add(p.p3.z)
        }
        ChristmasStrip.coords = lineCoords.toFloatArray()
        ChristmasStrip.dirty = true


    }

    private fun normalize(f: Float, max: Int): Float {
        return f/max.toFloat() - 0.5f
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
    fun getHighestBlueLocationFast(image: Bitmap): ChristmasPoint {
        val width = image.width
        val height = image.height
        var lowestDistance = Double.MAX_VALUE
        var lastColor = Color()
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        println("Image size: $width $height ")
        var p = Point()
        for (x in 0 until width)
            for (y in 0 until height) {
                val color = pixels[x + y*width]
                val colorDistance = getColorDistance(Color.RED, color)
                if (colorDistance<0.5)
                    if (colorDistance < lowestDistance) {
                        lowestDistance = colorDistance
                        lastColor = Color.valueOf(color)
                        p.x = x
                        p.y = y
                    }

            }
        return ChristmasPoint(p, lastColor, lowestDistance)
    }

    private fun findPoint(): ChristmasPoint {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture
        var christmasPoint = ChristmasPoint()
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

                christmasPoint = getHighestBlueLocationFast(bitmap)
                val canvas = Canvas(bitmap)
                val paint = Paint()
                paint.color = Color.RED
                paint.strokeWidth = 20f
                paint.style = Paint.Style.STROKE
                canvas.drawCircle(christmasPoint.p.x.toFloat(), christmasPoint.p.y.toFloat(), 125.0f, paint)

                calibrateActivity.imageView.setImageBitmap(bitmap)
                image.close()
                isTakingPhoto = false
            }
        })
        while (isTakingPhoto) Thread.sleep(10)

        return christmasPoint
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
