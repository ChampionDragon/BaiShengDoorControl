package com.bs.bean;

/**
 * 错误语句的bean类
 * 作者 Champion Dragon
 * created at 2017/6/29
 **/
public class ErrorBean {
    private int id;
    private long creattime;
    private String deviceError;

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

    public String getDeviceError() {
        return deviceError;
    }

    public void setDeviceError(String deviceError) {
        this.deviceError = deviceError;
    }

}
