package com.bs.Ezviz;

import android.app.Activity;
import android.util.Log;

import com.bs.base.BaseApplication;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZGlobalSDK;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAreaInfo;

import java.util.List;

/**
 * 界面跳转
 * 作者 Champion Dragon
 * created at 2017/6/22
 **/
public class ActivityUtils {
    /**
     * 处理token过期的错误
     */
    public static void handleSessionException(Activity activity) {
        goToLoginAgain(activity);
    }

    public static void goToLoginAgain(Activity activity) {
        Log.d("lcb", "ActivityUtils 28");
        if (EZGlobalSDK.class.isInstance(BaseApplication.getOpenSDK())) {
            Log.d("lcb", "ActivityUtils 30");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("lcb", "ActivityUtils 34");
                        List<EZAreaInfo> areaList = EZGlobalSDK.getInstance().getAreaList();
                        if (areaList != null) {
                            EZAreaInfo areaInfo = areaList.get(0);
                            EZGlobalSDK.getInstance().openLoginPage(areaInfo.getId());
                            Log.d("lcb", "ActivityUtils 40");
                        }
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            EZOpenSDK.getInstance().openLoginPage();
            Log.d("lcb", "ActivityUtils 49");
        }
    }
}
