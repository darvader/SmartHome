package com.darvader.smarthome.ledstrip

import android.media.MediaPlayer
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import com.darvader.smarthome.*
import kotlinx.android.synthetic.main.activity_led_strip.*
import java.io.UTFDataFormatException
import java.net.InetAddress
import java.util.ArrayList

class LedStrip(private val ledStripActivity: LedStripActivity): HomeElement {

    companion object {
        fun createColors(r: Int, g: Int, b: Int): ByteArray {
            val colors = ByteArray(3 * 12)
            for (i in 0 until colors.size / 3) {
                colors[i * 3] = r.toByte()
                colors[i * 3 + 1] = g.toByte()
                colors[i * 3 + 2] = b.toByte()
            }

            return colors
        }

        fun createColors(r: Byte, g: Byte, b: Byte, w: Byte): ByteArray {
            val colors = ByteArray(4 * 12)
            for (i in 0 until colors.size / 4) {
                colors[i * 4] = r
                colors[i * 4 + 1] = g
                colors[i * 4 + 2] = b
                colors[i * 4 + 3] = w
            }

            return colors
        }

        fun createColors(r: Int, g: Int, b: Int, r2: Int, g2: Int, b2: Int): ByteArray {
            val colors = ByteArray(3 * 12)
            for (i in 0 until colors.size /6) {
                colors[i * 3] = r.toByte()
                colors[i * 3 + 1] = g.toByte()
                colors[i * 3 + 2] = b.toByte()
            }
            for (i in colors.size / 6  until colors.size / 3) {
                colors[i * 3] = r2.toByte()
                colors[i * 3 + 1] = g2.toByte()
                colors[i * 3 + 2] = b2.toByte()
            }

            return colors

        }

        public var currentAddress = ""
        val echoClient = SmartHomeActivity.echoClient
    }

    private var red: Byte = 0
    private var green: Byte = 0
    private var blue: Byte = 0
    private var white: Byte = 0
    private val addresses = ArrayList<String>()
    private var buttonCounter = 1
    private val buttons = ArrayList<Button>()

    private val player = MediaPlayer()
    private val fft = FFT(1024)

    fun detect() {
        println("Detect called.")
        echoClient.sendBroadCast("Detect")
    }
    fun seven() {
        println("Seven called.")
        echoClient.send("seven", currentAddress)
    }
    fun white() {
        println("White called.")
        echoClient.send("white", currentAddress)
    }
    fun dot() {
        println("Dot called.")
        echoClient.send("dot", currentAddress)
    }
    fun rain() {
        println("Rain called.")
        echoClient.send("rain", currentAddress)
    }
    fun rainbow() {
        println("Rainbow called.")
        echoClient.send("rainbow", currentAddress)
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", currentAddress)
    }

    fun changeRed(red: Byte) {
        this.red = red
        val colors = createColors(red, green, blue, white)
        echoClient.send(colors, currentAddress)
    }

    fun changeGreen(green: Byte) {
        this.green = green
        val colors = createColors(red, green, blue, white)
        echoClient.send(colors, currentAddress)
    }

    fun changeBlue(blue: Byte) {
        this.blue = blue
        val colors = createColors(red, green, blue, white)
        echoClient.send(colors, currentAddress)
    }

    fun changeWhite(white: Byte) {
        this.white = white
        val colors = createColors(red, green, blue, white)
        echoClient.send(colors, currentAddress)
    }

    fun changeDivisor(divisor: Int) {
        var divi = divisor
        if (divi == 0)
            divi = 1
        echoClient.send("divide=" + divi.toChar(), currentAddress)
    }

    fun changeSpeed(speed: Int) {
        echoClient.send("speed=" + speed.toChar(), currentAddress)
    }

    fun fft() {
        println("fft.")
        echoClient.send("fft", currentAddress)
    }

    fun fftRow() {
        println("fftRow.")
        echoClient.send("fftRow", currentAddress)
    }

    fun christmasHorizontal() {
        println("christmasHorizontal.")
        echoClient.send("christmasHorizontal", currentAddress)
    }

    fun christmasVertical() {
        println("christmasVertical.")
        echoClient.send("christmasVertical", currentAddress)
    }

    fun christmasZ() {
        println("christmasZ.")
        echoClient.send("christmasZ", currentAddress)
    }

    fun christmasSevenH() {
        println("christmasSevenH.")
        echoClient.send("christmasSevenH", currentAddress)
    }

    fun christmasRotationY() {
        println("christmasRotationY.")
        echoClient.send("christmasRotationY", currentAddress)
    }

    fun christmasRotationX() {
        println("christmasRotationX.")
        echoClient.send("christmasRotationX", currentAddress)
    }

    fun christmasRotationZ() {
        println("christmasRotationZ.")
        echoClient.send("christmasRotationZ", currentAddress)
    }

    fun christmasSmoothRotationY() {
        println("christmasSmoothRotationY.")
        echoClient.send("christmasSmoothRotationY", currentAddress)
    }

    fun christmasSmoothRotationX() {
        println("christmasSmoothRotationX.")
        echoClient.send("christmasSmoothRotationX", currentAddress)
    }

    fun christmasSmoothRotationZ() {
        println("christmasSmoothRotationZ.")
        echoClient.send("christmasSmoothRotationZ", currentAddress)
    }

    fun fftRowRemote() {
        println("fftRowRemote.")
        echoClient.send("fftRowRemote", currentAddress)
    }

    fun fftRemote() {
        echoClient.send("remoteFFT", currentAddress)
    }

    override fun refresh(address: InetAddress, received: String) {
        val hostAddress = address.hostAddress
        if (received.startsWith("LedStrip")) {
            if (!addresses.contains(hostAddress)) {
                addresses.add(hostAddress)
                ledStripActivity.runOnUiThread { addButton(hostAddress) }
            }
        }
    }

    private fun addButton(address: String) {
        //set the properties for button
        val button = Button(ledStripActivity)
        button.layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)
        button.text = buttonCounter.toString()
        button.id = buttonCounter++
        button.setOnClickListener {
            currentAddress = address;
        }

        val layoutParams = button.layoutParams as ConstraintLayout.LayoutParams
        val size = buttons.size
        if (size == 0) {
            layoutParams.topToBottom = R.id.rain
        } else {
            if (size>=4) {
                layoutParams.topToBottom = buttons[size - 4].id
                layoutParams.leftToLeft = buttons[size - 4].id
            }
            else {
                layoutParams.topToBottom = R.id.rain
                layoutParams.leftToRight = buttons[size - 1].id
            }
        }
        //button.layoutParams = ConstraintLayout.LayoutParams(20, 10)
        this.buttons.add(button)
        //add button to the layout
        ledStripActivity.led_actitivy_layout.addView(button)
        println("button added")
    }

}
