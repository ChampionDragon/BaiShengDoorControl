package com.bs.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.bs.util.SmallUtil;

/**
 * 创建程序的基本类
 */
public class BaseActivity extends Activity {
    protected ProgressDialog mProgressDialog;
    public AsyncTaskExecutor executor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*设置成透明导航栏和透明状态栏*/
        SmallUtil.setScreen(this);
        executor = AsyncTaskExecutor.getinstance();
    }

    public void stopProgressDialog() {
        if (mProgressDialog.isShowing() && mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }
}
