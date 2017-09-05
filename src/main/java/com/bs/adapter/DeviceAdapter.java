package com.bs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.R;
import com.bs.bean.DeviceBean;
import com.bs.constant.LoadViewDevice;
import com.bs.util.ViewHolderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备的适配器
 * 作者 Champion Dragon
 * created at 2017/6/30
 **/

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceBean> list;
    private LoadViewDevice loadViewDevice;

    public DeviceAdapter(Context context, List<DeviceBean> list) {
        this.context = context;
        this.list = list;
        loadViewDevice = new LoadViewDevice(this);
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
        DeviceBean deviceBean = list.get(position);
        one.setText(deviceBean.getName());
        two.setText(deviceBean.getDeviceId());
//        Logs.d(getClass() + "  65   " + deviceBean.getDeviceId() + "   " + getItemId(position));
        loadViewDevice.showByAsyncTask(deviceBean, right);
        if (deviceBean.isOnline()) {
            right.setImageResource(R.drawable.wifionline);
            one.setTextColor(context.getResources().getColor(R.color.blueSky));
            two.setTextColor(context.getResources().getColor(R.color.blueSky));
        } else {
            right.setImageResource(R.drawable.wifi);
            one.setTextColor(context.getResources().getColor(R.color.black));
            two.setTextColor(context.getResources().getColor(R.color.black));
        }
        return convertView;
    }


    /**
     * 添加刷新的数据
     */
    public void setData(List<DeviceBean> deviceBeen) {
        list = deviceBeen;
    }


    /**
     * 将在线的设备置顶
     */
    private List<DeviceBean> getTopList(List<DeviceBean> list) {
        Map<Integer, DeviceBean> map = new HashMap();
        for (int i = list.size() - 1; i > 0; i--) {
            if (list.get(i).isOnline()) {
                map.put(i, list.get(i));
                list.remove(i);
            }
        }
        for (Integer key : map.keySet()) {
            list.add(0, map.get(key));
        }
        return list;
    }


    /**
     * 刷新数据
     */
    public void dataChange() {
        list = getTopList(list);
        setData(list);
        notifyDataSetChanged();
    }

    /**
     * 清空之前访问得缓存数据
     */
    public void cleanCache() {
        loadViewDevice = new LoadViewDevice(this);
    }
}
