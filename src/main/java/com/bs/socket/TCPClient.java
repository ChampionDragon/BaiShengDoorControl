package com.bs.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	private String serverIpAddress = "localhost";
    private int serverPort = 6000;
    private Socket dSocket = null;
    private String msg = "";
    //private byte[] bySendData = new byte[128];
    boolean bConnected = false;

    public static final int MSG_TCPREVEIVE = 10;
    Handler handler = null;

    public TCPClient() {
        super();
    }

    public TCPClient(String msg) {
        super();

        this.msg = msg;
    }

    public TCPClient(String serverIpAddress,
                     int serverPort,
                     String msg) {
        super();

        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
        this.msg = msg;
    }

    public void setServer(String serverIpAddress, int serverPort) {
        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isConnected() {
        return bConnected;
    }

    public String connect() {
        StringBuilder sb = new StringBuilder();

        try {
            dSocket = new Socket(serverIpAddress, serverPort);
            bConnected = true;
            sb.append("连接服务器成功").append("\n");
        } catch (UnknownHostException e) {
            sb.append("服务器连接失败.").append("\n");
            e.printStackTrace();
        } catch (IOException e) {
            sb.append("服务器连接失败.").append("\n");
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String disConnect() {
        StringBuilder sb = new StringBuilder();

        if (bConnected) {
            try {
                dSocket.close();
                bConnected = false;
                sb.append("断开服务器成功").append("\n");
            } catch (IOException e) {
                sb.append("断开服务器失败").append("\n");
                e.printStackTrace();
            }

        }

        return sb.toString();
    }

    public String send(byte[] data, int datalen) {
        if (datalen <= 0 || !bConnected) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try {
            OutputStream outputStream = dSocket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.write(data, 0, datalen);
            sb.append("消息发送成功!").append("\n");
        } catch (IOException e) {
            sb.append("消息发送失败.").append("\n");
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String send() {
        if (msg.isEmpty() || !bConnected) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try {
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(
                    dSocket.getOutputStream()));
            write.write(msg.replace("\n", " ") + "\n");
            write.flush();
            sb.append("消息发送成功!").append("\n");
        } catch (IOException e) {
            sb.append("消息发送失败.").append("\n");
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void receive() {
        if (!bConnected) {
            return;
        }

        try {
            InputStream inputStream = dSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            byte[] b = new byte[1024];

            while (true) {
                int length = dataInputStream.read(b);
                //String receiver = new String(b, 0, length, "gb2312");
                String receiver = new String(b);
                Log.v("data", receiver);

                if (handler != null) {
                    Message msg = new Message();
                    msg.what = MSG_TCPREVEIVE;

                    byte[] packet = new byte[length];
                    System.arraycopy(b, 0, packet, 0, length);
                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_STRING_TCPRECEIVER", receiver);
                    bundle.putByteArray("KEY_BYTEARRAY_TCPRECEIVER", packet);
                    msg.setData(bundle);

                    handler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
