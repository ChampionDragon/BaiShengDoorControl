package com.bs.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPServer {
    private int port = 8080;
    private ServerSocket serverSocket = null;
    private boolean hasStartListen = false; // 是否开始监听

    private Map<String, Client> mapClients = null;
    String tag = "lcb";
    Handler handler = null;

    // 消息定义
    public static final int TCPSERVER_START_LISTEN = 211;
    public static final int TCPSERVER_STOP_LISTEN = 222;
    public static final int TCPSERVER_SEND_SUCCESS = 223;
    public static final int TCPSERVER_SEND_FAIL = 224;
    public static final int TCPSERVER_RECEIVE = 225;
    public static final int TCPSERVER_CLIENT_CONNECTED = 226;
    public static final int TCPSERVER_CLIENT_DISCONNECTED = 227;
    public static final int TCPSERVER_START_LISTEN_FAIL = 228;

    public static final String SEND_BYTE = "KEY_BYTEARRAY_SENDER";
    public static final String CLIENT_IP = "KEY_STRING_CLIENTIP";


    public TCPServer(int port) {
        super();
        this.port = port;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean hasStartListen() {
        return hasStartListen;
    }

    // 开始监听
    public void startListen() {
        try {
            mapClients = new HashMap<String, Client>();
            serverSocket = new ServerSocket(port);
            hasStartListen = true;

            if (handler != null) {
                Message msg = new Message();
                msg.what = TCPSERVER_START_LISTEN;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (handler != null) {
                Message msg = new Message();
                msg.what = TCPSERVER_START_LISTEN_FAIL;
                handler.sendMessage(msg);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = null;
                Socket clientSocket = null;
                try {
                    while (hasStartListen) {
                        clientSocket = serverSocket.accept();

                        // 有客户端连接
                        if (handler != null) {
                            Message msg = new Message();
                            msg.what = TCPSERVER_CLIENT_CONNECTED;

                            Bundle bundle = new Bundle();
                            bundle.putString("KEY_STRING_CLIENTIP",
                                    clientSocket.getInetAddress().toString());
                            msg.setData(bundle);

                            handler.sendMessage(msg);
                        }

                        client = new Client(clientSocket);
                        mapClients.put(
                                clientSocket.getInetAddress().toString(),
                                client);
                    }// while
                } catch (IOException e) {
                    e.printStackTrace();

                    if (handler != null) {
                        Message msg = new Message();
                        msg.what = TCPSERVER_START_LISTEN_FAIL;
                        handler.sendMessage(msg);
                    }
                }
            }// run
        }).start();
    }

    // 停止监听
    public void stopListen() {
        try {
            serverSocket.close();

            hasStartListen = false;
            // mapClients.clear();
            clearClients();

            if (handler != null) {
                Message msg = new Message();
                msg.what = TCPSERVER_STOP_LISTEN;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 向某个客户端发送数据
    public void sendData(String strClientAddress, final byte[] data,
                         final int datalen) {
        if (!hasStartListen) {
            return;
        }

        Client client = mapClients.get(strClientAddress);
        if (client == null) {
            return;
        }

        client.send(data, datalen);
    }

    public void clearClients() {
        if (mapClients.size() <= 0)
            return;

        for (Client client : mapClients.values()) {
            client.close();
        }
        mapClients.clear();
    }

    public void sendData(String strClientAddress, String strData) {
        if (!hasStartListen) {
            return;
        }

        Client client = mapClients.get(strClientAddress);
        if (client == null) {
            return;
        }

        client.send(strData.getBytes(), strData.length());
    }

    // 向所有客户端发送数据
    public void sendData(final byte[] data, final int datalen) {
        if (!hasStartListen || mapClients.size() <= 0) {
            return;
        }

        for (Client client : mapClients.values()) {
            client.send(data, datalen);
        }
    }

    public void sendData(String strData) {
        if (!hasStartListen || mapClients.size() <= 0) {
            return;
        }

        for (Client client : mapClients.values()) {
            client.send(strData.getBytes(), strData.length());
        }
    }

    class Client {
        private Socket socket = null;

        public Client(Socket socket) {
            this.socket = socket;
            receive();
        }

        public void close() {
            if (socket == null)
                return;

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(final byte[] data, final int datalen) {
            if (socket == null || datalen <= 0) {
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /*
						 * BufferedWriter write = new BufferedWriter(new
						 * OutputStreamWriter( socket.getOutputStream()));
						 * write.write(data, 0, datalen); write.flush();
						 */

                        OutputStream outputStream = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(
                                outputStream);
                        dataOutputStream.write(data, 0, datalen);

                        if (handler != null) {
                            Message msg = new Message();
                            msg.what = TCPSERVER_SEND_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString(CLIENT_IP, socket
                                    .getInetAddress().toString());
                            //连接我的客户端的ip和自己的ip不一样
                            bundle.putByteArray(SEND_BYTE, data);
                            msg.setData(bundle);

                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        if (handler != null) {
                            Message msg = new Message();
                            msg.what = TCPSERVER_SEND_FAIL;
                            Bundle bundle = new Bundle();
                            bundle.putString("KEY_STRING_CLIENTIP", socket
                                    .getInetAddress().toString());
                            bundle.putByteArray("KEY_BYTEARRAY_SENDER", data);
                            msg.setData(bundle);

                            handler.sendMessage(msg);
                        }
                    }
                }
            }).start();
        }// send

        public void receive() {
            if (socket == null) {
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(
                                inputStream);
                        byte[] b = new byte[1024];

                        while (true) {//因为while(true)程序一直在运行
                            int length = dataInputStream.read(b);
                            if (length > 0) {// 接收到客户端数据
                                if (handler != null) {
                                    Message msg = new Message();
                                    msg.what = TCPSERVER_RECEIVE;
                                    byte[] data = new byte[length];
                                    System.arraycopy(b, 0, data, 0, length);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("KEY_STRING_CLIENTIP",
                                            socket.getInetAddress().toString());
                                    bundle.putByteArray(
                                            "KEY_BYTEARRAY_RECEIVER", data);
                                    msg.setData(bundle);

                                    handler.sendMessage(msg);
                                }
                            } else {// 客户端断开连接
                                if (handler != null) {
                                    Message msg = new Message();
                                    msg.what = TCPSERVER_CLIENT_DISCONNECTED;

                                    Bundle bundle = new Bundle();
                                    bundle.putString("KEY_STRING_CLIENTIP",
                                            socket.getInetAddress().toString());
                                    msg.setData(bundle);

                                    handler.sendMessage(msg);
                                }
                                mapClients.remove(socket.getInetAddress()
                                        .toString());
                                socket.close();
                            }// else
                        }// while
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }// run
            }).start();
        }
    }
}
