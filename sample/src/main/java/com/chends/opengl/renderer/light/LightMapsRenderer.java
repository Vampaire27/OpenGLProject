package com.chends.opengl.renderer.light;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 光照贴图
 * @author chends create on 2019/12/27.
 */
public class LightMapsRenderer extends BaseRenderer {
    private String vertexLightShaderCode, fragmentLightShaderCode;
    private float[] mLightPosInModelSpace = new float[]{0f, 0.4f, 1f, 1f};
    private final float[] mLightPosInWorldSpace = new float[4], mLightPosInEyeSpace = new float[4];

    private float[] mViewPos = new float[]{-1.5f, -1f, 4f, 1f};

    private float[] cubeCoords = new float[]{
            // 顶点               // 法向量          // 纹理坐标
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    public LightMapsRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_maps_vertex);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.light_maps_fragment);

        vertexLightShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec3 aPosition;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vec4(aPosition, 1.0);" +
                        " gl_PointSize = 35.0;" +
                        "}";
        fragmentLightShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        " gl_FragColor = vec4(1.0);" +
                        "}";
    }

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16],
            mLightMVPMatrix = new float[16], mLightModelMatrix = new float[16];
    private int diffuse, specular, emission;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_light_maps_image1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_light_maps_image2);
        Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_light_maps_image3);

        diffuse = OpenGLUtil.createTextureNormal(bitmap);
        specular = OpenGLUtil.createTextureNormal(bitmap2);
        emission = OpenGLUtil.createTextureNormal(bitmap3);
        bitmap.recycle();
        bitmap2.recycle();
        bitmap3.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        float angleInDegrees = (360.0f / 10000.0f) * (SystemClock.uptimeMillis() % 10000L);
        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 1.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);

        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, -4.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        drawCube();

        drawLight();
    }

    /**
     * 绘制立方体
     */
    private void drawCube() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, new String[]{
                "aPosition", "aNormal", "aTextCoords"});
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);
        // 法向量
        int normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);
        vertexBuffer.position(6);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);

        int mMVMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVMatrix");
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int materialDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.diffuse");
        int materialSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.specular");
        int materialEmissionPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.emission");
        int materialShininessPosHandle = GLES20.glGetUniformLocation(shaderProgram, "material.shininess");
        OpenGLUtil.bindTexture(materialDiffusePosHandle, diffuse, 0);
        OpenGLUtil.bindTexture(materialSpecularPosHandle, specular, 1);
        OpenGLUtil.bindTexture(materialEmissionPosHandle, emission, 2);
        GLES20.glUniform1f(materialShininessPosHandle, 32f);

        int lightAmbientPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.ambient");
        int lightDiffusePosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.diffuse");
        int lightSpecularPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.specular");
        int lightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "light.position");
        GLES20.glUniform3f(lightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glUniform3f(lightAmbientPosHandle, 0.2f, 0.2f, 0.2f);
        GLES20.glUniform3f(lightDiffusePosHandle, 0.5f, 0.5f, 0.5f);
        GLES20.glUniform3f(lightSpecularPosHandle, 1.0f, 1.0f, 1.0f);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cubeCoords.length / (3 + 3 + 2));

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }

    /**
     * 绘制光源
     */
    private void drawLight() {
        // ---------- 绘制光源 ---------------
        int lightProgram = OpenGLUtil.createProgram(vertexLightShaderCode, fragmentLightShaderCode);
        GLES20.glUseProgram(lightProgram);
        // 传入顶点坐标
        int lightPositionHandle = GLES20.glGetAttribLocation(lightProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(lightPositionHandle);
        GLES20.glVertexAttribPointer(lightPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, OpenGLUtil.createFloatBuffer(mLightPosInModelSpace));

        Matrix.multiplyMM(mLightMVPMatrix, 0, viewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mLightMVPMatrix, 0, projectionMatrix, 0, mLightMVPMatrix, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(lightProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mLightMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(lightPositionHandle);
    }
}