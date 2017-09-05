package com.bs.bean;

import java.util.List;

public class DeviceManagerBean {
	String data;
	List<ControlBean> list;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<ControlBean> getList() {
		return list;
	}

	public DeviceManagerBean(String data, List<ControlBean> list) {
		super();
		this.data = data;
		this.list = list;
	}

	public void setList(List<ControlBean> list) {
		this.list = list;
	}

}
