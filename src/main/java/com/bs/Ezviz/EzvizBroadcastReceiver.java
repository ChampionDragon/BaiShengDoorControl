package com.bs.Ezviz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bs.Ezviz.act.EZCameraListActivity;
import com.bs.R;
import com.bs.base.BaseApplication;
import com.bs.util.Logs;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.EZAccessToken;
import com.videogo.util.Utils;

/**
 * 萤石的广播接收
 * 作者 lcb
 * created at 2017/6/21
 **/

public class EzvizBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            EzvizAPI.getInstance().refreshNetwork();
        } else if (action.equals(Constant.ADD_DEVICE_SUCCESS_ACTION)) {
            String deviceId = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
            Utils.showToast(context, context.getString(R.string.device_is_added, deviceId));
        } else if (action.equals(Constant.OAUTH_SUCCESS_ACTION)) {
            Logs.i("onReceive: OAUTH_SUCCESS_ACTION       ezvizBroadcast34");
            Intent toIntent = new Intent(context, EZCameraListActivity.class);
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            /*******   获取登录成功之后的EZAccessToken对象   *****/
            EZAccessToken token = BaseApplication.getOpenSDK().getEZAccessToken();

            BaseApplication.sp.putString("萤石摄像头的秘钥",token.getAccessToken());

            Log.v("lcb", token.getAccessToken() + "      ezvizBroadcast42");

            context.startActivity(toIntent);
        }
    }
}