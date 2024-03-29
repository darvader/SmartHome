package com.darvader.smarthome.ledstrip.christmas;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {
    private FloatBuffer VertexBuffer;

    private final String VertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = uMVPMatrix * vPosition;" +
        "}";

    private final String FragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    protected int GlProgram;
    protected int PositionHandle;
    protected int ColorHandle;
    protected int MVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float LineCoords[] = {
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f
    };

    private final int VertexCount = LineCoords.length / COORDS_PER_VERTEX;
    private final int VertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    public Line() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
            // (number of coordinate values * 4 bytes per float)
            LineCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        VertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        VertexBuffer.put(LineCoords);
        // set the buffer to read the first coordinate
        VertexBuffer.position(0);

        int vertexShader = ChristmasStrip.Companion.loadShader(GLES30.GL_VERTEX_SHADER, VertexShaderCode);
        int fragmentShader = ChristmasStrip.Companion.loadShader(GLES30.GL_FRAGMENT_SHADER, FragmentShaderCode);

        GlProgram = GLES30.glCreateProgram();             // create empty OpenGL ES Program
        GLES30.glAttachShader(GlProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(GlProgram, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(GlProgram);                  // creates OpenGL ES program executables
    }

    public void SetVerts(float v0, float v1, float v2, float v3, float v4, float v5) {
        LineCoords[0] = v0;
        LineCoords[1] = v1;
        LineCoords[2] = v2;
        LineCoords[3] = v3;
        LineCoords[4] = v4;
        LineCoords[5] = v5;

        VertexBuffer.put(LineCoords);
        // set the buffer to read the first coordinate
        VertexBuffer.position(0);
    }

    public void SetColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES30.glUseProgram(GlProgram);

        // get handle to vertex shader's vPosition member
        PositionHandle = GLES30.glGetAttribLocation(GlProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(PositionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(PositionHandle, COORDS_PER_VERTEX,
                                 GLES30.GL_FLOAT, false,
                                 VertexStride, VertexBuffer);

        // get handle to fragment shader's vColor member
        ColorHandle = GLES30.glGetUniformLocation(GlProgram, "vColor");

        // Set color for drawing the triangle
        GLES30.glUniform4fv(ColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES30.glGetUniformLocation(GlProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, VertexCount);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(PositionHandle);
    }
 }