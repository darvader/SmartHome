package com.darvader.smarthome.ledstrip.christmas

import android.opengl.GLES20
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import com.darvader.smarthome.HomeElement
import com.darvader.smarthome.R
import com.darvader.smarthome.ledstrip.LedStrip
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.ArrayList

// number of coordinates per vertex in this array
class ChristmasStrip(private val christmasTreeActivity: ChristmasTreeActivity) : HomeElement {
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES20.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
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

    }

    private val addresses = ArrayList<String>()
    private var buttonCounter = 1
    private val buttons = ArrayList<Button>()
    var currentAddress = ""

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var mProgram: Int

    init {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(coords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(coords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private var vertexCount: Int = coords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun reload() {
        vertexBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(coords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(coords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
        vertexCount = coords.size / COORDS_PER_VERTEX
    }

    fun draw(mvpMatrix: FloatArray) { // pass in the calculated transformation matrix

        if (dirty) {
            reload()
            dirty = false
        }
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
                // get handle to shape's transformation matrix
                vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

                // Pass the projection and view transformation to the shader
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

                // Draw the triangle
                GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount)

                // Disable vertex array
                GLES20.glDisableVertexAttribArray(positionHandle)
            }

        }
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
            LedStrip.currentAddress = address;
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
        christmasTreeActivity.binding.ledActitivyLayout.addView(button)
        println("button added")
    }

    fun christmasHorizontal() {
        println("christmasHorizontal.")
        LedStrip.echoClient.send("christmasHorizontal", LedStrip.currentAddress)
    }

    fun christmasVertical() {
        println("christmasVertical.")
        LedStrip.echoClient.send("christmasVertical", LedStrip.currentAddress)
    }

    fun christmasZ() {
        println("christmasZ.")
        LedStrip.echoClient.send("christmasZ", LedStrip.currentAddress)
    }

    fun christmasSevenH() {
        println("christmasSevenH.")
        LedStrip.echoClient.send("christmasSevenH", LedStrip.currentAddress)
    }

    fun christmasRotationY() {
        println("christmasRotationY.")
        LedStrip.echoClient.send("christmasRotationY", LedStrip.currentAddress)
    }

    fun christmasRotationX() {
        println("christmasRotationX.")
        LedStrip.echoClient.send("christmasRotationX", LedStrip.currentAddress)
    }

    fun christmasRotationZ() {
        println("christmasRotationZ.")
        LedStrip.echoClient.send("christmasRotationZ", LedStrip.currentAddress)
    }

    fun christmasSmoothRotationY() {
        println("christmasSmoothRotationY.")
        LedStrip.echoClient.send("christmasSmoothRotationY", LedStrip.currentAddress)
    }

    fun christmasSmoothRotationX() {
        println("christmasSmoothRotationX.")
        LedStrip.echoClient.send("christmasSmoothRotationX", LedStrip.currentAddress)
    }

    fun christmasSmoothRotationZ() {
        println("christmasSmoothRotationZ.")
        LedStrip.echoClient.send("christmasSmoothRotationZ", LedStrip.currentAddress)
    }


}