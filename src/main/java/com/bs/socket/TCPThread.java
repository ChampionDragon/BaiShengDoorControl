package com.bs.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class TCPThread {
	private String serverIpAddress = "localhost";
	private int serverPort = 6000;
	String strResult = "";
	TCPClient client = null;

	public static final int MSG_TCPCONNECT_END = 4;
	public static final int MSG_TCPDISCONNECT_END = 5;
	public static final int MSG_TCPSEND_END = 6;
	public static final int MSG_TCPRECEIVE_START = 7;
	public static final int MSG_TCPRECEIVE_STOP = 8;
	private Handler handler = null;
	private boolean hasStartReceive = false;
	private ReceiveThread threasReceive = null;

	public TCPThread() {
		super();
	}

	public TCPThread(String serverIpAddress, int serverPort) {
		super();

		this.serverIpAddress = serverIpAddress;
		this.serverPort = serverPort;
	}

	public String getResult() {
		return strResult;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setServer(String serverIpAddress, int serverPort) {
		this.serverIpAddress = serverIpAddress;
		this.serverPort = serverPort;
	}

	public boolean isConnected() {
		if (client == null) {
			return false;
		}

		return client.isConnected();
	}

	public void connect() {
		if (client == null) {
			client = new TCPClient();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				client.setServer(serverIpAddress, serverPort);
				strResult = client.connect();

				if (handler != null) {
					Message msg = new Message();
					msg.what = MSG_TCPCONNECT_END;

					Bundle bundle = new Bundle();
					bundle.putString("KEY_STRING_CONNECTRESULT", strResult);
					msg.setData(bundle);

					handler.sendMessage(msg);
				}

				if (hasStartReceive) {
					startReceive();
				}
			}
		}).start();

		/*
		 * if (hasStartReceive) { if (threasReceive == null) { threasReceive =
		 * new ReceiveThread(); }
		 * 
		 * client.setHandler(handler); client.receive(); }
		 */
	}

	public void disCOnnect() {
		if (client == null || !client.isConnected()) {
			return;
		}

		stopReceive();

		new Thread(new Runnable() {
			@Override
			public void run() {
				strResult = client.disConnect();

				if (handler != null) {
					Message msg = new Message();
					msg.what = MSG_TCPDISCONNECT_END;

					Bundle bundle = new Bundle();
					bundle.putString("KEY_STRING_CONNECTRESULT", strResult);
					msg.setData(bundle);

					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	public void send(final String message) {
		if (client == null || !client.isConnected()) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TCPClient client = new TCPClient(message);
				// client.setServer(serverIpAddress, serverPort);
				client.setMsg(message);
				strResult = client.send();

				if (handler != null) {
					Message msg = new Message();
					msg.what = MSG_TCPSEND_END;

					Bundle bundle = new Bundle();
					bundle.putString("KEY_STRING_SENDRESULT", strResult);
					msg.setData(bundle);

					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	public void send(final byte[] data, final int datalen) {
		if (client == null || !client.isConnected()) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TCPClient client = new TCPClient(message);
				// client.setServer(serverIpAddress, serverPort);
				// client.setMsg(message);
				// strResult = client.send();
				strResult = client.send(data, datalen);

				if (handler != null) {
					Message msg = new Message();
					msg.what = MSG_TCPSEND_END;

					Bundle bundle = new Bundle();
					bundle.putString("KEY_STRING_SENDRESULT", strResult);
					msg.setData(bundle);

					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	public void startReceive() {
		hasStartReceive = true;

		if (client == null || !client.isConnected()) {
			return;
		}

		if (threasReceive == null) {
			threasReceive = new ReceiveThread();
		}

		client.setHandler(handler);
		threasReceive.start();

		if (handler != null) {
			Message msg = new Message();
			msg.what = MSG_TCPRECEIVE_START;
			handler.sendMessage(msg);
		}
	}

	public void stopReceive() {
		if (hasStartReceive) {
			// threasReceive.stop();
			threasReceive = null;
			hasStartReceive = false;

			if (handler != null) {
				Message msg = new Message();
				msg.what = MSG_TCPRECEIVE_STOP;
				handler.sendMessage(msg);
			}
		}
	}

	class ReceiveThread extends Thread {
		@Override
		public void run() {
			client.receive();
		}
	}
}
