package com.bs.guide;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bs.MainActivity;
import com.bs.R;
import com.bs.account.Login;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.SpKey;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;
import com.bs.util.SystemUtil;

public class Welcome extends BaseActivity {
    public int DELAYTIME = 2000;
    public static final int MAIN = 0;
    public static final int LOGIN = 1;
    public static final int GUIDE = 2;
    boolean isFisrt;
    boolean isLogin;
    private SpUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
        super.onCreate(savedInstanceState);
        sp=SpUtil.getInstance(SpKey.SP_device,MODE_PRIVATE);
        isFisrt = BaseApplication.sp.getBoolean(SpKey.isFirst, true);
        int curVer = SystemUtil.VersionCode();
        int preVer = BaseApplication.sp.getInt(SpKey.preVer);
        isLogin = sp.getBoolean(SpKey.isLogin);
        Logs.e("welcome 32 " + isLogin);
        //isFisrt || curVer > preVer     !isLogin
        if (isFisrt || curVer > preVer ) {
            handler.sendEmptyMessageDelayed(GUIDE, DELAYTIME);
            BaseApplication.sp.putInt(SpKey.preVer, curVer);
        } else if (!isLogin) {
            handler.sendEmptyMessageDelayed(LOGIN, DELAYTIME);
        } else {
            handler.sendEmptyMessageDelayed(MAIN, DELAYTIME);
        }

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAIN:
                    SmallUtil.getActivity(Welcome.this, MainActivity.class);
                    finish();
                    break;
                case LOGIN:
                    SmallUtil.getActivity(Welcome.this, Login.class);
                    finish();
                    break;
                case GUIDE:
                    SmallUtil.getActivity(Welcome.this, Guide.class);
                    finish();
                    break;

            }

        }
    };

}
