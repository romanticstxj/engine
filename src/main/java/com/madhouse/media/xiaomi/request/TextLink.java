package com.madhouse.media.xiaomi.request;

public class TextLink {
	private String id;
	private Integer[] battr;
	private Integer length;
	private Integer size;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer[] getBattr() {
		return battr;
	}

	public void setBattr(Integer[] battr) {
		this.battr = battr;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}
