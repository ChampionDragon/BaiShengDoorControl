package com.bs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bs.R;
import com.bs.activity.AboutUsActivity;
import com.bs.activity.AddDeviceActivity;
import com.bs.activity.CloudSaveActivity;
import com.bs.activity.ControlActivity;
import com.bs.activity.DeviceManager;
import com.bs.activity.HelpActivity;
import com.bs.activity.UpDateActivity;
import com.bs.util.SmallUtil;

public class MenuleftFragment extends Fragment implements OnClickListener {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_left, container, false);
        initView();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.menu_add).setOnClickListener(this);
        view.findViewById(R.id.menu_cloudsave).setOnClickListener(this);
        view.findViewById(R.id.menu_help).setOnClickListener(this);
        view.findViewById(R.id.menu_manage).setOnClickListener(this);
        view.findViewById(R.id.menu_us).setOnClickListener(this);
        view.findViewById(R.id.menu_update).setOnClickListener(this);
        view.findViewById(R.id.menu_push).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_manage:
                SmallUtil.getActivity(getActivity(), DeviceManager.class);
                break;
            case R.id.menu_add:
                SmallUtil.getActivity(getActivity(), AddDeviceActivity.class);
                break;
            case R.id.menu_push:
                SmallUtil.getActivity(getActivity(), ControlActivity.class);
                break;
            case R.id.menu_cloudsave:
                SmallUtil.getActivity(getActivity(), CloudSaveActivity.class);
                break;
            case R.id.menu_update:
//                SmallUtil.getActivity(getActivity(), SafeZoonActivity.class);
                SmallUtil.getActivity(getActivity(), UpDateActivity.class);
                break;
            case R.id.menu_help:
//                SmallUtil.getActivity(getActivity(), LogActivity.class);
                SmallUtil.getActivity(getActivity(), HelpActivity.class);
                break;
            case R.id.menu_us:
                SmallUtil.getActivity(getActivity(), AboutUsActivity.class);
                break;
        }
    }
}
