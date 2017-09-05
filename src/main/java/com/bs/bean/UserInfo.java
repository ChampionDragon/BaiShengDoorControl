package com.bs.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {
	String name;
	String SSID;
	String headpath;

	public String getHeadpath() {
		return headpath;
	}

	public void setHeadpath(String headpath) {
		this.headpath = headpath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}
}