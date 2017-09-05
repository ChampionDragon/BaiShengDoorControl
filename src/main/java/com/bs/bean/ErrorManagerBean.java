package com.bs.bean;

import java.util.List;

public class ErrorManagerBean {
	String data;
	List<ErrorBean> list;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<ErrorBean> getList() {
		return list;
	}

	public ErrorManagerBean(String data, List<ErrorBean> list) {
		super();
		this.data = data;
		this.list = list;
	}

	public void setList(List<ErrorBean> list) {
		this.list = list;
	}
}
