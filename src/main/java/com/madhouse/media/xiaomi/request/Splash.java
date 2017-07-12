package com.madhouse.media.xiaomi.request;


public class Splash {
	private Integer w;
	private Integer h;
	private Integer skip;
	private Integer duration;
	private Integer detailpos;
	private Integer[] btype;
	private Integer[] battr;
	private String[] mines;
	private Integer frequencycapping = 0;

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

	public Integer getSkip() {
		return skip;
	}

	public void setSkip(Integer skip) {
		this.skip = skip;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDetailpos() {
		return detailpos;
	}

	public void setDetailpos(Integer detailpos) {
		this.detailpos = detailpos;
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

	public String[] getMines() {
		return mines;
	}

	public void setMines(String[] mines) {
		this.mines = mines;
	}

	public Integer getFrequencycapping() {
		return frequencycapping;
	}

	public void setFrequencycapping(Integer frequencycapping) {
		this.frequencycapping = frequencycapping;
	}

}
