package com.bs.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 重写SurfaceView,解决不能截图的问题
 * Created by Administrator on 2017/6/15.
 */

public class MySurfaceVivew extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Bitmap bitmap;

    public MySurfaceVivew(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = this.getHolder();
    }

    private void drawCanvas(Bitmap bitmap) {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.drawCanvas(bitmap);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.drawCanvas(bitmap);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
