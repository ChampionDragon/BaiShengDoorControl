package com.bs.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bs.constant.SpKey;
import com.bs.util.SpUtil;
import com.bs.util.ToastUtil;
import com.videogo.openapi.EZOpenSDK;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class BaseApplication extends Application {
    private static BaseApplication mInstance = null;
    public static Context context;
    public static String mServerUrl="http://s.tingchebao.com/zld/";//停车宝服务器地址
    public static SpUtil sp;
    public static WifiManager.MulticastLock lock;
    public static WifiManager wfm;
    public static RequestQueue queue;
    String tag = "BaseApplication";
    private long firsttime;
    //开发者需要填入自己申请的appkey
    public static String AppKey = "edba025e588d48949dbf607af6aa82ec";


    public static BaseApplication getInstance() {
        if (mInstance == null) {
            mInstance = new BaseApplication();
        }
        return mInstance;
    }


    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        mInstance = this;
        context = getApplicationContext();
        sp = SpUtil.getInstance(SpKey.SP_name, MODE_PRIVATE);
        initUDP();
        initOkHttp();
        initEzviz();
        /*初始化RequestQueue*/
        queue = Volley.newRequestQueue(context);
    }

    /**
     * 初始化okHttp
     */
    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    /**
     * 初始化萤石摄像头SDK
     */
    private void initEzviz() {
        /**
         * sdk日志开关，正式发布需要去掉
         */
//        EZOpenSDK.showSDKLog(true);

        /**
         * 设置是否支持P2P取流,详见api
         */
        EZOpenSDK.enableP2P(false);

        /**
         * APP_KEY请替换成自己申请的
         */
        EZOpenSDK.initLib(this, AppKey, "");
    }


    private void initUDP() {
        // 有的手机不能直接接收UDP包，可能是手机厂商在定制Rom的时候把这个功能给关掉了。实例化一个WifiManager.MulticastLock
        // 对象lock, 在调用广播发送、接收报文之前先调用lock.acquire()方法；
        // 用完之后及时调用lock.release()释放资源，否决多次调用lock.acquire()方法，程序可能会崩.
        wfm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        lock = wfm.createMulticastLock("wifi lcb");
    }


    /**
     * 退出整个程序
     */
    public void exitApp() {
        long secondtime = System.currentTimeMillis();
        if (secondtime - firsttime > 1000) {
            ToastUtil.showLong("再点一次就退出哦！");
            firsttime = secondtime;
        } else {
            onTerminate();
        }
    }

    @Override
    public void onTerminate() {
        //杀死栈里面的所有Activity
        finishAllActivity();

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());

        System.exit(0);
        super.onTerminate();
    }


    /* ---------------------------------自定义管理栈------------------------------------------------------------- */
    private static Stack<Activity> activityStack;

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


}
