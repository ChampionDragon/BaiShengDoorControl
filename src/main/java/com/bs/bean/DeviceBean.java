package com.bs.bean;

/**
 * 设备的bean类
 * 作者 Champion Dragon
 * created at 2017/6/29
 **/

public class DeviceBean {
    int id;
    String DeviceId;
    String Name="";
    String Address="";
    String CreateTime="";
    int Number;
    String flagOne;
    String flagTwo;
    boolean online;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public String getFlagOne() {
        return flagOne;
    }

    public void setFlagOne(String flagOne) {
        this.flagOne = flagOne;
    }

    public String getFlagTwo() {
        return flagTwo;
    }

    public void setFlagTwo(String flagTwo) {
        this.flagTwo = flagTwo;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
