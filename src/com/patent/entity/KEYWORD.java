package com.patent.entity;

public class KEYWORD {
	private String k1;
	private String k2;	
	private float value;
	
	public String getK1() {
		return k1;
	}

	public void setK1(String k1) {
		this.k1 = k1;
	}

	public String getK2() {
		return k2;
	}

	public void setK2(String k2) {
		this.k2 = k2;
	}

	

	public KEYWORD(String k1, String k2, float v) {
		this.k1 = k1;
		this.k2 = k2;
		this.value = v;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
