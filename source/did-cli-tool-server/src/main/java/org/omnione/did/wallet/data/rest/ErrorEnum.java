package org.omnione.did.wallet.data.rest;

public enum ErrorEnum {

	SUCCESS(200, "success"), FAIL(500, "fail"), NONE(0, "none");

	private int code;
	private String desc;

	ErrorEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
