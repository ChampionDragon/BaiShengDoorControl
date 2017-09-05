package com.bs.bean;

/**
 * 控制语句的bean类
 * 作者 Champion Dragon
 * created at 2017/6/29
 **/
public class ControlBean {
    private int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private int id;
    private long creattime;
    private String deviceControl;
    private String deviceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreattime() {
        return creattime;
    }

    public void setCreattime(long creattime) {
        this.creattime = creattime;
    }

    public String getDeviceControl() {
        return deviceControl;
    }

    public void setDeviceControl(String deviceControl) {
        this.deviceControl = deviceControl;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ControlBean(long creattime, String deviceControl, String deviceName) {
        super();
        this.creattime = creattime;
        this.deviceControl = deviceControl;
        this.deviceName = deviceName;
    }

    public ControlBean() {

    }
}
