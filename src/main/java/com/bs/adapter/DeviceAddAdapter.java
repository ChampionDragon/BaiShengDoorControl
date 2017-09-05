package com.bs.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.R;
import com.bs.util.ViewHolderUtil;

import java.util.List;

public class DeviceAddAdapter extends BaseAdapter {
	private Context context;
	private List<ScanResult> list;
	String tag = "lcb";

	public DeviceAddAdapter(Context context, List<ScanResult> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_mainlv, null);
		}
		TextView one = ViewHolderUtil.get(convertView, R.id.mainlv_tv_one);
		TextView two = ViewHolderUtil.get(convertView, R.id.mainlv_tv_two);
		ImageView left = ViewHolderUtil.get(convertView, R.id.mainlv_iv_left);
		ImageView right = ViewHolderUtil.get(convertView, R.id.mainlv_iv_level);

//		Log.d(tag, one + "    " + two);
//		Log.d(tag, left + "    " + right);
//		Log.e(tag, convertView + "");

		ScanResult scanResult = list.get(position);
		String ssid = scanResult.SSID;
		two.setText(ssid);
		WifiManager manger = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manger.getConnectionInfo();
		String ssidInfo = info.getSSID().replace("\"", "");
		if (ssid.equals(ssidInfo)) {
			right.setImageResource(R.drawable.wifionline);
			one.setTextColor(context.getResources().getColor(R.color.blueSky));
			two.setTextColor(context.getResources().getColor(R.color.blueSky));
		} else {
			one.setTextColor(context.getResources().getColor(R.color.black));
			two.setTextColor(context.getResources().getColor(R.color.black));
			int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
			if (scanResult.capabilities.contains("WEP")
					|| scanResult.capabilities.contains("PSK")
					|| scanResult.capabilities.contains("EAP")) {
				right.setImageResource(R.drawable.wifi_signal_lock);
			} else {
				right.setImageResource(R.drawable.wifi_signal_open);
			}
			// 判断信号强度，显示对应的指示图标  
			right.setImageLevel(level);
		}

		return convertView;
	}

	/**
	 * 添加刷新的数据
	 */
	public void setData(List<ScanResult> scanResults) {
		list = scanResults;
	}

}
