package com.bs.constant;

import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.bs.adapter.DeviceAdapter;
import com.bs.bean.DeviceBean;
import com.bs.http.HttpByGet;
import com.bs.util.Logs;

import java.util.Random;

/**
 * DeviceAapter的异步加载帮助类
 * 作者 Champion Dragon
 * created at 2017/7/6
 **/

public class LoadViewDevice {
    private LruCache<String, String> lruCache;
    private ListView lv;
    private DeviceAdapter deviceAdapter;


    public LoadViewDevice(DeviceAdapter adapter) {
        deviceAdapter = adapter;
        // 获取本APP可调用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cachaSize = maxMemory / 8;
        // 创建LruCache对象,同时用匿名内部类的方式重写方法
        lruCache = new LruCache<String, String>(cachaSize) {
            @Override
            protected int sizeOf(String key, String value) {
//                return super.sizeOf(key, value);
                //返回字符串的实际长度
                return value.length();
            }
        };
    }

    /**
     * 存储数据到LruCache
     */
    private void addToCache(String key, String value) {
        if (getfromCache(key) == null) {
            lruCache.put(key, value);
        }
    }

    /**
     * 从LruCache得到数据
     */
    private String getfromCache(String key) {
        return lruCache.get(key);
    }


    /**
     * 将传入的数据就给后台请求并做相应处理
     */
    public void showByAsyncTask(DeviceBean bean, ImageView imageView) {
        String key = bean.getDeviceId();
        String value = getfromCache(key);
        Logs.d(key + "  LoadViewDevice 65  " + value + "    " + bean.isOnline());
        if (value == null) {
            new NewsAsyncTask().execute(bean);
        }
//        else {
//            if (value.equals("NO RESPONSE")) {
//                bean.setOnline(false);
//            } else {
//                bean.setOnline(true);
//            }
//        }
    }


//     ======================================使用AsyncTask==================================
    private class NewsAsyncTask extends AsyncTask<DeviceBean, Void, String> {

        @Override
        protected String doInBackground(DeviceBean... params) {

            DeviceBean bean = params[0];
            String ID = bean.getDeviceId();
            int seq = new Random().nextInt(1000);

              /*通过查询id状态的方式判定设备书否在线*/
//            String cmd = "cmd:102,type:1,termID:" + ID + ",seq:" + seq
//                    + ",flag:2,param:test";
//            byte[] cmdBytes = cmd.getBytes();
//            String s = UdpUtil.ServerSend(cmdBytes);
//            Logs.v("LoadViewDevice97   "+cmd+"\n"+s);

            /*通过查询设备是否在线*/
            String url = Constant.isOnline + HttpByGet.get("deviceid", ID + ".");
            String s = HttpByGet.executeHttpGet(url);

            Logs.v("LoadViewDevice102  " + url + "\n" + s + "   length" + s.length());
//            读取字符串的第二个字符，然后和0做比较
//            Character a='0';
//            char c = s.charAt(1);
//            Logs.d("LoadViewDevice108   "+c+"    "+a.equals(c));


            addToCache(ID, s);
            if (s.equals(" 1")) {  //返回的数据里有空格,所以1前面我空了一个格
                bean.setOnline(true);
            } else {
                bean.setOnline(false);
            }
            return s;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.indexOf("1") >= 0) {
                deviceAdapter.dataChange();
            }
        }
    }


}
