package com.bs.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.db.DbManager;
import com.bs.socket.Protocol;
import com.bs.socket.TCPServer;
import com.bs.util.DialogNotileUtil;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;

import java.util.Arrays;
import java.util.Random;

import static com.bs.socket.TCPServer.TCPSERVER_CLIENT_DISCONNECTED;

public class DeviceControlActivity extends BaseActivity {
	private TCPServer tcpServer = null;
	private int nPort = Constant.tcpServerPort;// 设备出场默认开放的端口号
	private String tag = "lcb";
	private TextView txtSSID;
	private TextView txtLocalIpAddress;
	private TextView txtResult;
	private TextView txtDeviceState;
	private ImageView iv;
	private Button btnGetDeviceState, btnOpenDevice, btnCloseDevice;
	private Button btnTestRes;
	private String strSSID = null;

	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private int sasa = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		tcpServer = new TCPServer(nPort);
		tcpServer.setHandler(handler);
		tcpServer.startListen();
		setupComponent();
		IntentFilter filter = new IntentFilter(
				WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(controlReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(controlReceiver);
		tcpServer.stopListen();
	}

	private void setupComponent() {
		txtSSID = (TextView) findViewById(R.id.txtSSID);
		txtLocalIpAddress = (TextView) findViewById(R.id.txtLocalIpAddress);
		txtResult = (TextView) findViewById(R.id.txtResult);
		txtDeviceState = (TextView) findViewById(R.id.txtDeviceState);
		btnGetDeviceState = (Button) findViewById(R.id.btnGetDeviceState);
		btnOpenDevice = (Button) findViewById(R.id.btnOpenDevice);
		btnCloseDevice = (Button) findViewById(R.id.btnCloseDevice);
		btnTestRes = (Button) findViewById(R.id.btnTestRes);
		iv = (ImageView) findViewById(R.id.back_control);
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnGetDeviceState.setOnClickListener(btnGetDeviceStateClkLis);
		btnOpenDevice.setOnClickListener(btnOpenDeviceClkLis);
		btnCloseDevice.setOnClickListener(btnCloseDeviceClkLis);
		btnTestRes.setOnClickListener(btnTestResClkLis);

		initwifiInfo();

	}

	/**
	 * 重置连接信息
	 */
	private void initwifiInfo() {
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		wifiInfo = wifiManager.getConnectionInfo();
		strSSID = wifiInfo.getSSID();
		int ipAddress = wifiInfo.getIpAddress();
		String strLocalIpAddress = IntToIp(ipAddress);
		strSSID = strSSID.replace("\"", "");
		txtSSID.setText(strSSID);
		txtLocalIpAddress.setText("本地IP地址:  " + strLocalIpAddress);
	}

	private String IntToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	// 获取设备状态
	private Button.OnClickListener btnGetDeviceStateClkLis = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			byte[] packet = Protocol.getDeviceReqPacket("LIGHT:?");
			Log.d(tag, "req：             " + Arrays.toString(packet));
			tcpServer.sendData(packet, packet.length);
			initError();
		}
	};

	// 打开设备
	private Button.OnClickListener btnOpenDeviceClkLis = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			byte[] packet = Protocol.getDeviceReqPacket("LIGHT:1");
			Log.e(tag, "open：             "+Arrays.toString(packet));
			tcpServer.sendData(packet, packet.length);

		}
	};

	// 关闭设备
	private Button.OnClickListener btnCloseDeviceClkLis = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			byte[] packet = Protocol.getDeviceReqPacket("LIGHT:0");
			Log.i(tag, "close：             "+Arrays.toString(packet));
			// Log.d("long",Arrays.toString(packet));
			// byte[] sasa=Protocol.getDeviceResData(packet,packet.length);
			// Log.v("long",Arrays.toString(sasa));
			// String aa = SmallUtil.Bytes2hexStr(sasa);
			// String sasa = SmallUtil.hexStr2Str(aa);
			// Log.w("long",aa);
			// Log.i("long",sasa);
			tcpServer.sendData(packet, packet.length);
		}
	};

	// 发送响应数据包作测试（LIGHT:1）
	private Button.OnClickListener btnTestResClkLis = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			byte[] packet = Protocol.getDeviceResPacket("LIGHT:1");
			tcpServer.sendData(packet, packet.length);
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String strResult = txtResult.getText().toString();
			switch (msg.what) {
			case TCPServer.TCPSERVER_START_LISTEN:
				txtResult.setText(strResult + "开启监听成功！\n");
				break;
			case TCPServer.TCPSERVER_START_LISTEN_FAIL:
				txtResult.setText(strResult + "开启监听失败！\n");
				break;
			case TCPServer.TCPSERVER_STOP_LISTEN:
				txtResult.setText(strResult + "停止监听！\n");
				break;
			case TCPServer.TCPSERVER_CLIENT_CONNECTED: {
				String strAddress = msg.getData().getString(
						"KEY_STRING_CLIENTIP");
				txtResult
						.setText(strResult + "客户端：" + strAddress + "  连接成功！\n");
				txtDeviceState.setText("已连接");
				txtDeviceState.setTextColor(Color.BLACK);
			}
				break;
			case TCPSERVER_CLIENT_DISCONNECTED: {
				String strAddress = msg.getData().getString(
						"KEY_STRING_CLIENTIP");
				txtResult
						.setText(strResult + "客户端：" + strAddress + "  断开连接！\n");

				txtDeviceState.setText("未连接");
				txtDeviceState.setTextColor(Color.RED);
			}
				break;
			case TCPServer.TCPSERVER_SEND_SUCCESS:
				String ip = msg.getData().getString(TCPServer.CLIENT_IP);
				byte[] send = msg.getData().getByteArray(TCPServer.SEND_BYTE);
				break;
			case TCPServer.TCPSERVER_RECEIVE: {
				txtResult
						.setText(txtResult.getText().toString() + "接收到响应数据！\n");
				byte[] packet = msg.getData().getByteArray(
						"KEY_BYTEARRAY_RECEIVER");// 接收回来的数据
				String hexStr = SmallUtil.Bytes2hexStr(packet);
				// Log.d(TAG, hexStr);
				String ss = SmallUtil.hexStr2Str(hexStr, "gb2312");
				Log.w(tag, "DeCon208     recevice   " + ss);
				// 添加数据到数据库
				/*
				 * Random random = new Random(); String[] name = { "超级用户lcb",
				 * "超级用户cyp", "普通用户lxp", "遥控" }; SpUtil spUtil =
				 * SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE); int a =
				 * spUtil.getInt(SpKey.deviceControl); DbManager manager =
				 * DbManager.getmInstance( DeviceControlActivity.this,
				 * Constant.dbDiveceBsmk, Constant.dbVersion);
				 * manager.addOrUpdateControl(a, ss, name[random.nextInt(4)],
				 * System.currentTimeMillis());
				 * spUtil.putInt(SpKey.deviceControl, a+1);
				 */

				// 打印到textview上
				String strPacket = ss + "|| len = "
						+ new String(Integer.toString(packet.length));
				txtResult.setText(txtResult.getText().toString() + strPacket
						+ "\n");

				byte[] data = Protocol.getDeviceResData(packet, packet.length);
				if (data == null) {
					break;
				}

				String strRes = SmallUtil.Bytes2hexStr(data);// bytes转换成十六进制字符串
				String str = SmallUtil.hexStr2Str(strRes, "gb2312");
				Log.i(tag, strRes + " DeCon234  " + str);
				if (strRes.equals("LIGHT:1")) {
					txtDeviceState.setText("打开");
				} else if (strRes.equals("LIGHT:0")) {
					txtDeviceState.setText("关闭");
				} else {
					txtDeviceState.setText("未知");
				}
			}
				break;
			}
		}
	};

	// 监听wifi状态变化
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = manager
			// .getActiveNetworkInfo();
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			NetworkInfo.State state = networkInfo.getState();// 得到当前网络的状态
			if (NetworkInfo.State.DISCONNECTED == state) {
				txtDeviceState.setText("设备断开");
				txtSSID.setText("设备断开");
				txtLocalIpAddress.setText("设备断开");
			}
			if (networkInfo.isConnected()) {
				wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				initwifiInfo();
				DialogNotileUtil.show(DeviceControlActivity.this, strSSID
						+ "连接成功");
			}
		}

	};

	/**
	 * 添加随机错误信息
	 */
	protected void initError() {
		Random random = new Random();
		String[] error = { "关门遇阻", "开门遇阻", "开启异常", "关闭异常" };
		SpUtil spUtil = SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE);
		DbManager manager = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
				Constant.dbVersion);
		int a = spUtil.getInt(SpKey.deviceError);
		manager.addOrUpdateError(a, error[random.nextInt(4)],
				System.currentTimeMillis());
		spUtil.putInt(SpKey.deviceError, a + 1);
	}

}
