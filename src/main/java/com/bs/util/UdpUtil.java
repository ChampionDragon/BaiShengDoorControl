package com.bs.util;

import android.net.wifi.WifiManager;

import com.bs.base.BaseApplication;
import com.bs.constant.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * UDP发送指令的封装类
 * 作者 Champion Dragon
 * created at 2017/7/5
 **/

public class UdpUtil {
    private static DatagramSocket dSocket;
    private static InetAddress inetAddress;
    private static WifiManager.MulticastLock lock = BaseApplication.lock;

    public static String Send(String ip, int port, byte[] data) {
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Logs.i("udputil 25    " + e);
        }
        try {
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(10000);// 如果对方连接状态10秒没有收到数据的话强制断开客户端

        } catch (SocketException e) {
            e.printStackTrace();
            Logs.i("udputil 32    " + e);
        }
        DatagramPacket dPacket = new DatagramPacket(data, data.length,
                inetAddress, port);
        try {
            dSocket.send(dPacket);
            Logs.i(dPacket.getAddress() + "  udputil 45  " + dPacket.getPort());
//            Logs.i("udputil 47    " + Arrays.toString(dPacket.getData()));
        } catch (IOException e) {
            e.printStackTrace();
            Logs.i("udputil 50    " + e);
        }
        String s = Recevice();
        return s;
    }

    private static String Recevice() {
        String result = "NO RESPONSE";
        byte[] dataRec = new byte[66];
        DatagramPacket dPacket = new DatagramPacket(dataRec, dataRec.length);
        try {
            lock.acquire();
            dSocket.receive(dPacket);
            lock.release();
            result = new String(dPacket.getData());
//            Logs.i("udputil 66          " + result + "\n" + Arrays.toString(dPacket.getData()));
        } catch (IOException e) {
            e.printStackTrace();
            Logs.i("udputil 69    " + e);
        }
        dSocket.close();
        return result;
    }


    /**
     * 通过字节数组包的接口
     */
    public static String IpSend(byte[] data) {
        return Send(Constant.ipIP, Constant.ipPort, data);
    }

    /**
     * 通过字符串的接口
     */
    public static String ServerSend(byte[] data) {
        return Send(Constant.serverIP, Constant.serverPort, data);

    }


}
