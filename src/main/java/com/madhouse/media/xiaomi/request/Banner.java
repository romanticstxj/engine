package com.madhouse.media.xiaomi.request;

public class Banner {
	private Integer w;
	private Integer h;
	private String id;
	private Integer[] btype;
	private Integer[] battr;
	private Integer embedding = 0;
	private String[] mines;

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public Integer getH() {
		return h;
	}

	public void setH(Integer h) {
		this.h = h;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer[] getBtype() {
		return btype;
	}

	public void setBtype(Integer[] btype) {
		this.btype = btype;
	}

	public Integer[] getBattr() {
		return battr;
	}

	public void setBattr(Integer[] battr) {
		this.battr = battr;
	}

	public Integer getEmbedding() {
		return embedding;
	}

	public void setEmbedding(Integer embedding) {
		this.embedding = embedding;
	}

	public String[] getMines() {
		return mines;
	}

	public void setMines(String[] mines) {
		this.mines = mines;
	}

}
