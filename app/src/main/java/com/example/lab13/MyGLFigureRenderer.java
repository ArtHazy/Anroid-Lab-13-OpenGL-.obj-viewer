package com.example.lab13;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLFigureRenderer implements GLSurfaceView.Renderer {
    private MyFigure myFigure;
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private float[] rotationMatrix = new float[16];

    public static int loadShader(int type, String vertexShaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,vertexShaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        myFigure = new MyFigure(MainActivity.obj);
    }
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        // Матрица проекции применяется к координатам фигуры в методе onDrawFrame();
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float[] scratch = new float[16];
        // Задаём позицию камеры (матрицу вида)
        Matrix.setLookAtM(viewMatrix, 0, 1f, 4f, 3f, 0f, 0f, 0f, 0f, 1.0f,
                0.0f);
        // Вычисляем преобразования с помощью матриц проекции и вида
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.09f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, angle, angle, angle, -1.0f);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
        myFigure.draw(scratch);
    }
}
