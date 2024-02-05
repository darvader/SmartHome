package com.darvader.smarthome.ledstrip.christmas

import android.opengl.GLES30
import android.util.Log
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import com.darvader.smarthome.HomeElement
import com.darvader.smarthome.SmartHomeActivity
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// number of coordinates per vertex in this array
class ChristmasStrip() : HomeElement {
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
            return GLES30.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES30.glShaderSource(shader, shaderCode)
                GLES30.glCompileShader(shader)
            }
        }
        var coords = floatArrayOf(     // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f,      // top
            -0.5f, -0.311004243f, 0.0f,    // bottom left
            -0.5f, -0.311004243f, 0.0f,    // bottom left
            0.5f, -0.311004243f, 0.0f,      // bottom right
            0.5f, -0.311004243f, 0.0f,      // bottom right
            0.0f, 0.622008459f, 0.0f      // top
        )
        var dirty = false
        var currentAddress = ""
        val echoClient = SmartHomeActivity.echoClient
    }

    lateinit var christmasTreeActivity: ChristmasTreeActivity
    private val addresses = ArrayList<String>()
    private var buttonCounter = 1
    private val buttons = ArrayList<Button>()
    var currentAddress = ""

    init {

    }

    override fun refresh(address: InetAddress, received: String) {
        val hostAddress = address.hostAddress
        if (received.startsWith("LedStrip")) {
            if (!addresses.contains(hostAddress)) {
                addresses.add(hostAddress)
                christmasTreeActivity.runOnUiThread { addButton(hostAddress) }
            }
        }
    }

    private fun addButton(address: String) {
        //set the properties for button
        val button = Button(christmasTreeActivity)
        button.layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)
        button.text = address.substring(address.length - 3)
        button.id = buttonCounter++
        button.setOnClickListener {
            ChristmasStrip.currentAddress = address;
        }

        val layoutParams = button.layoutParams as ConstraintLayout.LayoutParams
        val size = buttons.size
        if (size == 0) {
            layoutParams.topToBottom = christmasTreeActivity.binding.christmasRotationZ.id
        } else {
            if (size>=4) {
                layoutParams.topToBottom = buttons[size - 4].id
                layoutParams.leftToLeft = buttons[size - 4].id
            }
            else {
                layoutParams.topToBottom = christmasTreeActivity.binding.christmasRotationZ.id
                layoutParams.leftToRight = buttons[size - 1].id
            }
        }
        //button.layoutParams = ConstraintLayout.LayoutParams(20, 10)
        this.buttons.add(button)
        //add button to the layout
        christmasTreeActivity.binding.layout.addView(button)
        currentAddress = address
        println("button added")
    }

    fun christmasHorizontal() {
        println("christmasHorizontal.")
        echoClient.send("christmasHorizontal", ChristmasStrip.currentAddress)
    }

    fun christmasVertical() {
        println("christmasVertical.")
        echoClient.send("christmasVertical", ChristmasStrip.currentAddress)
    }

    fun christmasZ() {
        println("christmasZ.")
        echoClient.send("christmasZ", ChristmasStrip.currentAddress)
    }

    fun christmasSevenH() {
        println("christmasSevenH.")
        echoClient.send("christmasSevenH", ChristmasStrip.currentAddress)
    }

    fun christmasRotationY() {
        println("christmasRotationY.")
        echoClient.send("christmasRotationY", ChristmasStrip.currentAddress)
    }

    fun christmasRotationX() {
        println("christmasRotationX.")
        echoClient.send("christmasRotationX", ChristmasStrip.currentAddress)
    }

    fun christmasRotationZ() {
        println("christmasRotationZ.")
        echoClient.send("christmasRotationZ", ChristmasStrip.currentAddress)
    }

    fun christmasSmoothRotationY() {
        println("christmasSmoothRotationY.")
        echoClient.send("christmasSmoothRotationY", ChristmasStrip.currentAddress)
    }

    fun christmasSmoothRotationX() {
        println("christmasSmoothRotationX.")
        echoClient.send("christmasSmoothRotationX", ChristmasStrip.currentAddress)
    }

    fun christmasSmoothRotationZ() {
        println("christmasSmoothRotationZ.")
        echoClient.send("christmasSmoothRotationZ", ChristmasStrip.currentAddress)
    }
    fun detect() {
        println("Detect called.")
        echoClient.sendBroadCast("Detect")
    }
}