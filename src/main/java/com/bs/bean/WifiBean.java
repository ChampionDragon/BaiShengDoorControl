package com.bs.bean;

public class WifiBean {
    String key = "";
    String value = "";
    boolean check;

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WifiBean(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }
}
