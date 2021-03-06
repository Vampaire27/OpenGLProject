package com.chends.opengl.view.window;

import android.content.Context;

import com.chends.opengl.renderer.window.TriangleMatrixRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2019/12/10.
 */
public class TriangleMatrixView extends BaseGLView {

    public TriangleMatrixView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new TriangleMatrixRenderer());
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
