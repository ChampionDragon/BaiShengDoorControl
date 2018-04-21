package com.bs.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.R;
import com.bs.activity.DeviceManager;
import com.bs.activity.DeviceSet;
import com.bs.bean.WifiBean;
import com.bs.constant.Constant;
import com.bs.db.DbHelper;
import com.bs.db.DbManager;
import com.bs.util.DialogCustomUtil;
import com.bs.util.SmallUtil;
import com.bs.util.ViewHolderUtil;

import java.util.List;

public class DeviceSetAdapter extends BaseAdapter {
    private Activity context;
    private List<WifiBean> list;
    private Dialog dialog;

    public DeviceSetAdapter(DeviceManager context, List<WifiBean> list) {
        super();
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
                    R.layout.item_managelv, null);
        }
        WifiBean wifiBean = list.get(position);
        final String key = wifiBean.getKey();
        final String value = wifiBean.getValue();
        final int location = position;

        TextView one = ViewHolderUtil.get(convertView, R.id.managelv_one);
        TextView two = ViewHolderUtil.get(convertView, R.id.managelv_two);
        ImageView set = ViewHolderUtil.get(convertView, R.id.managelv_set);
        ImageView delete = ViewHolderUtil
                .get(convertView, R.id.managelv_delete);

        //Log.d(tag, one+"    "+two);
        //final View view = convertView;

        one.setText(wifiBean.getKey());
        two.setText(wifiBean.getValue());
        set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(context, DeviceSet.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("name", key);
//				bundle.putInt("position", location);
//				intent.putExtras(bundle);
//				context.startActivityForResult(intent, DeviceManager.NAME);
                Bundle bundle = new Bundle();
                bundle.putString("id", value);
                SmallUtil.getActivity(context, DeviceSet.class, bundle);
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = DialogCustomUtil.create("警告", "你确定要从列表里删除这个设备?",
                        context, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                DbManager manager = DbManager.getmInstance(context, Constant.dbDiveceBsmk, Constant.dbVersion);
                                manager.cleanDevice(DbHelper.DEVICE_ID + "=\"" + value + "\"");
                                list.remove(location);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }

        });

        return convertView;
    }

    /**
     * 添加刷新的数据
     */
    public void setData(List<WifiBean> scanResults) {
        list = scanResults;
    }

}
