package com.bs.socket;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bs.constant.Constant;
import com.bs.util.Logs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

/**
 * UDP传输线程
 * 作者 lcb created at 2017/5/13
 **/

public class UDPThread {
    // 服务器地址
    public String svrIpAddress;
    // 服务器端口
    public int svrPort;
    private DatagramSocket dSocket = null;
    private InetAddress inetAddress = null;
    private WifiManager.MulticastLock lock;
    private Handler handler = null;
    ReceiveThread receiveThread = null;
    WifiManager wifiManager;
    private Map map;
    private long first;

    private boolean hasConnected = false;
    private boolean hasStartReceive = false;
    public static final int MSG_UDP_CONNECTED = 21;
    public static final int MSG_UDP_DISCONNECTED = 22;
    public static final int MSG_UDP_SEND = 23;
    public static final int MSG_UDP_RECEIVE = 24;

    public static final String KEYUDPRECIP = "KEY_STRING_UDPRECIP";
    public static final String KEYUDPRECPORT = "KEY_INT_UDPRECPORT";
    public static final String KEYUDPRECEIVE = "KEY_STRING_UDPRECEIVE";


    public UDPThread(Handler handler, WifiManager manager) {
        super();
        wifiManager = manager;
        lock = wifiManager.createMulticastLock("WIFIUPDDemo");
        this.handler = handler;
    }

    public UDPThread() {
        super();
    }

    /**
     * 设置ip,port值
     */
    public void setServerPara(String svrIpAddress, int svrPort) {
        this.svrIpAddress = svrIpAddress;
        this.svrPort = svrPort;
    }
    // public void setHandler(Handler handler) {
    // this.handler = handler;
    // }

    public boolean isConnected() {
        return hasConnected;
    }

    public String connect() {
        StringBuilder sb = new StringBuilder();
        try {
            inetAddress = InetAddress.getByName(svrIpAddress);
        } catch (UnknownHostException e) {
            sb.append("未找到服务器.").append("\n");
            e.printStackTrace();
        }
        try {
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(10000);// 如果对方连接状态10秒没有收到数据的话强制断开客户端
            sb.append("服务器连接成功").append("\n");
            hasConnected = true;
            hasStartReceive = true;

        } catch (SocketException e) {
            sb.append("服务器连接失败.").append("\n");
            Logs.e("udpthread90    " + e);
            e.printStackTrace();
        }

        if (hasStartReceive) {
            startReceive();
        }

        return sb.toString();
    }

    public String disConnect() {
        if (!hasConnected) {
            return "服务器断开成功";
        }
        stopReceive();

        dSocket.close();
        hasConnected = false;

        return "服务器断开成功";
    }

    public void send(final byte[] data) {
        if (!hasConnected || data.length <= 0) {
            return;
        }
        final String ip = svrIpAddress;
        final int port = svrPort;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int datalen = data.length;
                try {
                    inetAddress = InetAddress.getByName(ip);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                DatagramPacket dPacket = new DatagramPacket(data, datalen,
                        inetAddress, port);
                Logs.d(inetAddress + "  udpthread134  " + port);

                try {
                    dSocket.send(dPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return;
    }

    public void startReceive() {
        hasStartReceive = true;

        if (!isConnected()) {
            return;
        }

        if (receiveThread == null) {
            receiveThread = new ReceiveThread();
        }

        receiveThread.start();
    }

    public void stopReceive() {
        hasStartReceive = false;
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            if (!isConnected()) {
                return;
            }

            byte[] dataRec = new byte[66];
            DatagramPacket dPacket = new DatagramPacket(dataRec, dataRec.length);
            while (hasStartReceive) {
                try {
                    lock.acquire();
                    dSocket.receive(dPacket);
                    lock.release();

                    if (handler != null) {
                        String strAddress = dPacket.getAddress()
                                .getHostAddress();
                        int nPort = dPacket.getPort();
                        byte[] data = dPacket.getData();
//                        String bytes2hexStr = SmallUtil.Bytes2hexStr(data);
                        String strReceiver = new String(data);
//                        Logs.d("udpthread187    "+strReceiver);
//                        Logs.d("udpthread188    "+ Arrays.toString(data));
                        // new String(dPacket.getData(), dPacket.getOffset(),
                        // dPacket.getLength());
//                        Logs.d(bytes2hexStr + "\n"+"186     "
//                                + SmallUtil.hexStr2Str(bytes2hexStr, "gb2312"));

//                        Message msg = new Message();
                        Message msg = Message.obtain();
                        msg.what = MSG_UDP_RECEIVE;

//                        Message msg = handler.obtainMessage(MSG_UDP_RECEIVE);
                        Bundle bundle = new Bundle();
                        bundle.putString(KEYUDPRECIP, strAddress);
                        bundle.putInt(KEYUDPRECPORT, nPort);
                        bundle.putString(KEYUDPRECEIVE, strReceiver+"\n"+Arrays.toString(data));
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    long second = System.currentTimeMillis();
                    if (second - first > Constant.bgTimeout) {
                        Logs.e("udpthread205        " + e);
                        first = second;
                    }
//                    if (handler != null) {
//                        Message msg = handler.obtainMessage(MSG_UDP_RECEIVE);
//                        Bundle bundle = new Bundle();
//                        bundle.putString(KEYUDPRECEIVE, e + "");
//                        msg.setData(bundle);
//                        handler.sendMessage(msg);
//                    }
                }
            }
        }
    }

}
