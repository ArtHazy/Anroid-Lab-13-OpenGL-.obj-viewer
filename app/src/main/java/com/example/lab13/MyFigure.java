package com.example.lab13;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;

public class MyFigure {
    private final int myProgram;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    static final int COORDS_PER_VERTEX = 3;

    static float squareCoords[];
    float color[] = {0.5f, 0.7f, 0.5f, 1.0f};

    public MyFigure(Obj obj) {
        IntBuffer indices = ObjData.getFaceVertexIndices(obj);
        FloatBuffer vertices = ObjData.getVertices(obj);
//        FloatBuffer texCoords = ObjData.getTexCoords(obj,2);
//        FloatBuffer normals = ObjData.getNormals(obj);

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.remaining()*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.remaining()*2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        for (int i = 0; i < indices.remaining(); i++) {
            drawListBuffer.put((short) indices.get(i));
        }
        drawListBuffer.position(0);

        int vertexShader = MyGLFigureRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLFigureRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        myProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(myProgram, vertexShader);
        GLES20.glAttachShader(myProgram, fragmentShader);
        GLES20.glLinkProgram(myProgram);
    }

    private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    " gl_Position = uMVPMatrix * vPosition;" +
                    " gl_PointSize = 10.0; "+
                    "}";
    private final String fragmentShaderCode =
                    "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";


    private int positionHandle, colorHandle, vPMatrixHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(myProgram);

        positionHandle = GLES20.glGetAttribLocation(myProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,GLES20.GL_FLOAT,false,vertexStride,vertexBuffer);

        colorHandle = GLES20.glGetUniformLocation(myProgram,"vColor");
        GLES20.glUniform4fv(colorHandle,1,color,0);

        vPMatrixHandle = GLES20.glGetUniformLocation(myProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle,1,false,mvpMatrix,0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawListBuffer.remaining(),GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
