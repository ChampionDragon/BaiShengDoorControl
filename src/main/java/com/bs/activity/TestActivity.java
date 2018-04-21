package com.bs.activity;

import android.os.Bundle;
import android.util.Log;

import com.bs.base.BaseActivity;
import com.bs.bean.ControlBean;
import com.bs.bean.DeviceBean;
import com.bs.bean.DeviceManagerBean;
import com.bs.bean.ErrorBean;
import com.bs.constant.Constant;
import com.bs.db.DbHelper;
import com.bs.db.DbManager;
import com.bs.socket.TestProtocol;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.TimeUtil;
import com.bs.util.UdpUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TestActivity extends BaseActivity {
    String tag = "lcb";
    DbManager manager;
    List<ErrorBean> listError;
    List<ControlBean> listControl;
    List<DeviceBean> listDevice;
    byte[] packet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
//         test();
        text1();
        //test();
        // text2(listControl);
        // text3();
    }


    /*跳转到控制界面*/
    private void getControlActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("id", Constant.idFOUR);
        SmallUtil.getActivity(TestActivity.this, ControlOneActivity.class, bundle);
    }


    /*查询设备ID*/
    private void findDevcieID() {
        packet = TestProtocol.getByCmd("1", "", TestProtocol.cmdID);
        Logs.d("aboutUs54   " + Arrays.toString(packet));
        executor.submit(IdRunnable);

    }

    Runnable IdRunnable = new Runnable() {
        @Override
        public void run() {
            String serverSend = UdpUtil.IpSend(packet);
            Logs.e("aboutUs64   " + serverSend);

        }
    };


    private void text3() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(tag, "aboutUs74");
                    Socket socket = new Socket(Constant.serverIP,
                            Constant.serverPort);
                    Log.v(tag, 1 + socket.toString());
                } catch (UnknownHostException e) {
                    Log.d(tag, 2 + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(tag, 3 + e.getMessage());
                }

            }
        }).start();
    }

    private void addData() {
        long time = System.currentTimeMillis();
        manager.addOrUpdateDevice("a", "lcb", "南昌航空大学", TimeUtil.long2time((time - 6666), Constant.cformatsecond));
        manager.addOrUpdateDevice("b", "lxp", "南昌金属公司", TimeUtil.long2time(time, Constant.cformatsecond));
        manager.addOrUpdateDevice("c", "cyp", "江西拖拉机厂", TimeUtil.long2time((time + 6666), Constant.cformatsecond));
    }

    private void init() {
        manager = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
                Constant.dbVersion);
        listError = manager.getErrorList();
        listControl = manager.getControlList();
        String s = "c";
        String ss = "";
        manager.cleanDevice(DbHelper.ID + "=?", new String[]{"22"});
//        addData();
        listDevice = manager.getDeviceList();
//        Logs.d(listDevice.size() + "  83");
    }


    private void text2(List<ControlBean> list) {
        Map<String, List<ControlBean>> map = new HashMap<>();
        List<ControlBean> deviceBeans = null;
        List<DeviceManagerBean> deviceManagerBeans = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ControlBean bean = list.get(i);
            String key = "";
            String data = TimeUtil.long2time(bean.getCreattime(),
                    Constant.cformatD);
            boolean b = key.equals(data);
            if (!b) {
                deviceBeans = new ArrayList<>();
                key = data;
            }
            deviceBeans.add(bean);
            map.put(key, deviceBeans);
        }
        Set<Entry<String, List<ControlBean>>> entrySet = map.entrySet();
        for (Entry<String, List<ControlBean>> entry : entrySet) {
            Log.d(tag, entry.getKey() + "   " + entry.getValue());
            deviceManagerBeans.add(new DeviceManagerBean(entry.getKey(), entry
                    .getValue()));
        }
        Log.d(tag, deviceManagerBeans.size() + "");
    }

    private void text1() {
        // manager.cleanControl(DbHelper.DEVICE_CONTROL_ID + " in(47,45,46)",
        // null);
        // 同时删除n个id
        for (DeviceBean bean : listDevice) {
            Logs.e(bean.getDeviceId() + "    " + bean.getId() + "    " + bean.getName());
        }
//        for (ControlBean bean : listControl) {
//            Log.d(tag,
//                    bean.get_id()
//                            + "   " + bean.getId() + "   "
//                            + bean.getDeviceName() + "  "
//                            + bean.getDeviceControl()+"   "
//                            + TimeUtil.long2time(bean.getCreattime(),
//                            Constant.formatsecond));
//        }
//		 for (ErrorBean error : listError) {
//		 Log.e(tag,
//		 error.getId()
//		 + "  "
//		 + error.getDeviceError()
//		 + "    "
//		 + TimeUtil.long2time(error.getCreattime(),
//		 Constant.formatsecond));
//		 }
    }


    private void test() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("lcb", "haha");
        map.put("cyp", "lala");
        map.put("lxp", "huhu");
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String value = map.get(key);
            Log.d(tag, key + "  " + value);
        }
        map = new HashMap<>();
        map.put("lcb", "haha");
        map.put("cyp", "lala");
        map.put("lxp", "huhu");
        Set<Entry<String, String>> entrySet = map.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.e(tag, key + "  " + value);
        }

    }


}
