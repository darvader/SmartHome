package com.darvader.smarthome.matrix

import android.graphics.Bitmap
import androidx.core.graphics.get
import com.darvader.smarthome.HomeElement
import com.darvader.smarthome.matrix.activity.ScoreboardActivity
import com.darvader.smarthome.SmartHomeActivity
import java.net.InetAddress

class LedMatrix(): HomeElement {
    companion object {
        var matrixAddress = ""
        val echoClient = SmartHomeActivity.echoClient
        val buttonAddresses = ArrayList<String>()
    }
    private var time: Int = 0
    private var invert = true
    private var timeoutThread: Thread? = null
    var pointsLeft: Byte = 0
    var pointsRight: Byte = 0
    var setsLeft: Byte = 0
    var setsRight: Byte = 0
    var leftTeamServes: Byte = 1
    var scoreboardActivity: ScoreboardActivity? = null

    fun detect() {
        println("Detect called.")
        val start = System.currentTimeMillis()
        echoClient.sendBroadCast("Detect")
    }

    private fun updateText() {
        this.scoreboardActivity!!.binding.points.text = "${pointsLeft.toString().padStart(2, '0')}:${pointsRight.toString().padStart(2, '0')}"
        this.scoreboardActivity!!.binding.sets.text = "$setsLeft:$setsRight"
        if (leftTeamServes == 1.toByte()) {
            scoreboardActivity!!.binding.ballLeft.setBackgroundColor(0xff404000.toInt())
            scoreboardActivity!!.binding.ballRight.setBackgroundColor(0xffffffff.toInt())
        } else {
            scoreboardActivity!!.binding.ballRight.setBackgroundColor(0xff404000.toInt())
            scoreboardActivity!!.binding.ballLeft.setBackgroundColor(0xffffffff.toInt())
        }
    }

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("LedMatrix")) {
            val hostAddress = address.hostAddress
            println("Got LedMatrix: $hostAddress")
            matrixAddress = hostAddress
        }
        if (received.startsWith("PushButton")) {
            buttonAddresses += address.hostAddress
        }
    }

    private fun updateScore() {
        updateText()
        if (invert)
            send("updateScore=${pointsRight.toChar()}${pointsLeft.toChar()}${setsRight.toChar()}${setsLeft.toChar()}${(1-leftTeamServes).toChar()}")
        else
            send("updateScore=${pointsLeft.toChar()}${pointsRight.toChar()}${setsLeft.toChar()}${setsRight.toChar()}${leftTeamServes.toChar()}")
    }

    fun pointsLeftUp() {
        if (pointsLeft >= 99) return
        pointsLeft++
        leftTeamServes = 1
        updateScore()
    }

    fun pointsLeftDown() {
        updateText()
        if (pointsLeft <= 0) return
        pointsLeft--
        leftTeamServes = 0
        updateScore()
    }

    fun pointsRightUp() {
        if (pointsRight >= 99) return
        pointsRight++;
        leftTeamServes = 0
        updateScore()
    }

    fun pointsRightDown() {
        if (pointsRight <= 0) return
        pointsRight--;
        leftTeamServes = 1
        updateScore()
    }

    fun ballLeft() {
        leftTeamServes = 1
        updateScore()
    }

    fun ballRight() {
        leftTeamServes = 0
        updateScore()
    }

    fun clearPoints() {
        pointsLeft = 0
        pointsRight = 0
        leftTeamServes = 1
        updateScore()
    }

    fun reset() {
        pointsLeft = 0
        pointsRight = 0
        setsLeft = 0
        setsRight = 0
        leftTeamServes = 0
        updateScore()
    }

    fun send(message: String) {
        echoClient.send(message, matrixAddress)
    }

    fun send(message: ByteArray) {
        echoClient.send(message, matrixAddress)
    }

    fun off() {
        send("off")
    }

    fun setsLeftUp() {
        if (setsLeft >= 9) return
        setsLeft++
        updateScore()
    }

    fun setsLeftDown() {
        if (setsLeft <= 0) return
        setsLeft--
        updateScore()
    }

    fun setsRightUp() {
        if (setsRight >= 9) return
        setsRight++
        updateScore()
    }

    fun setsRightDown() {
        if (setsRight <= 0) return
        setsRight--
        updateScore()
    }

    fun switch() {
        pointsLeft = pointsRight.also { pointsRight = pointsLeft }
        setsLeft = setsRight.also { setsRight = setsLeft }
        if (leftTeamServes == 0.toByte()) {
            leftTeamServes = 1
        } else if (leftTeamServes == 1.toByte()) {
            leftTeamServes = 0
        }
        updateScore()
    }

    fun timeout() {
        send("timeout")
        Thread {
            val start = System.currentTimeMillis()
            var elapsed = System.currentTimeMillis() - start
            while (elapsed < 30000) {
                elapsed = System.currentTimeMillis() - start
                scoreboardActivity?.runOnUiThread {
                    scoreboardActivity?.binding?.timeoutBar?.progress = (elapsed / 30000.0 * 100).toInt()
                }
                Thread.sleep(100)
            }
            scoreboardActivity?.runOnUiThread {
                scoreboardActivity?.binding?.timeoutBar?.progress = 0
            }
        }.start()

    }

    fun setScrollText(s: CharSequence?) {
        if (s!!.length <= 1) return
        send("scrollText=$s")
    }

    fun startTime1() {
        send("time1")
    }

    fun startTime2() {
        send("time2")
    }

    fun startTime3() {
        send("time3")
    }

    fun startTime4() {
        send("time4")
    }

    fun startSnow() {
        send("timeSnow")
    }

    fun startScoreboard() {
        send("scoreboard")
    }

    fun mandelbrot() {
        send("mandelbrot")
    }

    fun changeBrightness(brightness: Int) {
        send("brightness=${brightness.toChar()}")
    }

    fun invert() {
        invert = !invert
    }

    fun pictureMode() {
        send("picture")
    }

    fun convertRGBto565(c: Int): Int {
        val red = (c shr 16) and 0xFF
        val green = (c shr 8) and 0xFF
        val blue = c and 0xFF

        val b =  (blue  shr 3)        and 0x001F
        val g = ((green shr 2) shl  5) and 0x07E0 // not <
        val r = ((red   shr 3) shl 11) and 0xF800 // not <

        return (r or g or b);
    }

    var byteArray: ByteArray = ByteArray(64*32*3)

    fun sendBitmap(bitmap: Bitmap) {
        var i = 0
        for (x in 0 until 64)
            for (y in 0 until 32) {
                if (x >= bitmap.width || y >=bitmap.height) {
                    byteArray[i++] = 0
                    byteArray[i++] = 0
                    byteArray[i++] = 0
                    continue
                }
                val c = bitmap.get(x, y)
                // val color = convertRGBto565(c)

                byteArray[i++] = (c shr 16).toByte()
                byteArray[i++] = (c shr 8).toByte()
                byteArray[i++] = (c and 0xff).toByte()
            }
        echoClient.send(byteArray, matrixAddress)
        println("Bitmap send.")
    }

    enum class TimerMode {
        timer, stopWatch;
    }

    fun setTime(time: Int) {
        this.time = time
        val highChar = (time shr 8).toByte()
        val lowChar = (time and 0xff).toByte()
        val bytes = "timer=".toByteArray(Charsets.UTF_8) + highChar + lowChar
        send(bytes)
        var mode = TimerMode.timer

        println("Timer send with highchar: ${highChar} and lowChar: ${lowChar}")
    }

    fun startTimer() {
        mode = TimerMode.timer
        send("timerStart")
    }

    fun pauseTimer() {
        send("timerPause")
    }

    fun stopWatch() {
        mode = TimerMode.stopWatch
        send("stopWatch")
    }

    fun stopWatchStart() {
        send("stopWatchStart")
    }

    fun stopWatchStop() {
        send("stopWatchStop")
    }

    var buttonPressedNr = 0;
    var mode = TimerMode.timer

    fun received(address: InetAddress?, received: String) {
        if (received.contains("ButtonPressed")) {
            if (mode == TimerMode.timer) {
                startTimer()
            } else if (mode == TimerMode.stopWatch) {
                if (buttonPressedNr % 2 == 0)
                    stopWatchStart()
                else
                    stopWatchStop()
            }
            buttonPressedNr++;
        }

    }
}
