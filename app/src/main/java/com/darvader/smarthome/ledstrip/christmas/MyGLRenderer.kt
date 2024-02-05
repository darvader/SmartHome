package com.darvader.smarthome.ledstrip.christmas

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var tree: ChristmasStrip
    private val rotationMatrix = FloatArray(16)

    @Volatile
    var angle: Float = 0f
    private var mProgram: Int = -1
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

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(ChristmasStrip.coords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(ChristmasStrip.coords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private var vertexCount: Int = ChristmasStrip.coords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)



    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        val vertexShader: Int = ChristmasStrip.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int =
            ChristmasStrip.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            // Handle the error
            Log.e("OpenGL Error", "Could not create the program: GL Error Code: $error")
        }

        GLES30.glLinkProgram(mProgram)

// Check the link status
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] != GLES30.GL_TRUE) {
            // If linking failed, delete the program
            Log.e("OpenGL Error", "Could not link program: ")
            Log.e("OpenGL Error", GLES30.glGetProgramInfoLog(mProgram))
            GLES30.glDeleteProgram(mProgram)
            mProgram = 0
        }
        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        // initialize a triangle
        tree = ChristmasTreeActivity.tree
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 6f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Create a rotation transformation for the triangle
        val scratch = FloatArray(16)

        // Create a rotation for the triangle
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, -1f, 0f)

        val scratch2 = FloatArray(16)
        // Combine the rotation matrix with the projection and camera view
        // Note that the mvpMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, rotationMatrix, 0)

        Matrix.setRotateM(rotationMatrix, 0, 180.0f, 0f, 0f, -1f)
        Matrix.multiplyMM(scratch, 0, scratch, 0, rotationMatrix, 0)
        Matrix.translateM(scratch, 0, 0f, -0.2f, 0f)
        Matrix.scaleM(scratch, 0, 2f, 3.5f, 2f)

        // Draw triangle
        draw(scratch)
    }

    private fun reload() {
        vertexBuffer =
                // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(ChristmasStrip.coords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(ChristmasStrip.coords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }
        vertexCount = ChristmasStrip.coords.size / COORDS_PER_VERTEX
    }
    fun draw(mvpMatrix: FloatArray) { // pass in the calculated transformation matrix

        if (ChristmasStrip.dirty) {
            reload()
            ChristmasStrip.dirty = false
        }
        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES30.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
                // get handle to shape's transformation matrix
                vPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")

                // Pass the projection and view transformation to the shader
                GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

                // Enable a handle to the vertices
                GLES30.glEnableVertexAttribArray(0)
                // Draw the triangle
                GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, vertexCount)

                // Disable vertex array
                GLES30.glDisableVertexAttribArray(positionHandle)
            }
        }
    }


    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}