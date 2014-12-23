package com.reyun.utils;

import java.util.Arrays;

import org.json.JSONObject;

public class Record {

	private int id = 0;
	private String name = null;
	private JSONObject value = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JSONObject getValue() {
		return value;
	}

	public void setValue(JSONObject value) {
		this.value = value;
	}

}
