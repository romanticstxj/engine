package com.madhouse.media.xiaomi.request;

public class Video {
	private String[] mines;
	private Integer minduration;
	private Integer maxduration;
	private Integer[] protocols;
	private Integer w;
	private Integer h;
	private float size;
	private Integer[] battr;
	private Integer frequencycapping = 0;

	public String[] getMines() {
		return mines;
	}

	public void setMines(String[] mines) {
		this.mines = mines;
	}

	public Integer getMinduration() {
		return minduration;
	}

	public void setMinduration(Integer minduration) {
		this.minduration = minduration;
	}

	public Integer getMaxduration() {
		return maxduration;
	}

	public void setMaxduration(Integer maxduration) {
		this.maxduration = maxduration;
	}

	public Integer[] getProtocols() {
		return protocols;
	}

	public void setProtocols(Integer[] protocols) {
		this.protocols = protocols;
	}

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

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Integer[] getBattr() {
		return battr;
	}

	public void setBattr(Integer[] battr) {
		this.battr = battr;
	}

	public Integer getFrequencycapping() {
		return frequencycapping;
	}

	public void setFrequencycapping(Integer frequencycapping) {
		this.frequencycapping = frequencycapping;
	}

}

