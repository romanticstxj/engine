package com.madhouse.dsp.iflytek;

import java.util.List;


public class IflytekResponse {
	// 公共信息
	private String rc;//
	private String info_en;
	private String info_cn;
	private String matype;

	// 普通广告信息（ html 格式） 公共信息中的 matype 取值为”html”
	private String adtype;// 广告交互类型
	private String html;// html 内容
	private String package_name;// 下载包名称
	private String[] impr_url;// 曝光监控数组
	private String[] click_url;// 点击监控数组
	private String[] inst_downstart_url;// 针对下载类广告
	private String[] inst_downsucc_url;// 针对下载类广告
	private String[] inst_installstart_url;//
	private String[] inst_installsucc_url;//

	// 普通广告信息（ json 格式） 当下发内容为 json 格式普通广告时， 公共信息中的 matype 取值为”meta” /  原生广告
	private Integer batch_cnt;// 本次下发的广告数目
	private List<MaterialObject> batch_ma;// 广告列表

	// 音视频广告信息 当下发内容为音视频广告时， 公共信息中的 matype 取值为”meta”
	private String url;// url of video or audio file
	private String duration;// duration of video or audio
	private String landing_url;
	private String deep_link;
	private String[] start_url;// 音视频开始播放的监控链接
	private String[] over_url;// 音视频播放完毕的监控链接
	private String ad_source_mark;// 广告来源

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getInfo_en() {
		return info_en;
	}

	public void setInfo_en(String info_en) {
		this.info_en = info_en;
	}

	public String getInfo_cn() {
		return info_cn;
	}

	public void setInfo_cn(String info_cn) {
		this.info_cn = info_cn;
	}

	public String getMatype() {
		return matype;
	}

	public void setMatype(String matype) {
		this.matype = matype;
	}

	public String getAdtype() {
		return adtype;
	}

	public void setAdtype(String adtype) {
		this.adtype = adtype;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String[] getImpr_url() {
		return impr_url;
	}

	public void setImpr_url(String[] impr_url) {
		this.impr_url = impr_url;
	}

	public String[] getClick_url() {
		return click_url;
	}

	public void setClick_url(String[] click_url) {
		this.click_url = click_url;
	}

	public String[] getInst_downstart_url() {
		return inst_downstart_url;
	}

	public void setInst_downstart_url(String[] inst_downstart_url) {
		this.inst_downstart_url = inst_downstart_url;
	}

	public String[] getInst_downsucc_url() {
		return inst_downsucc_url;
	}

	public void setInst_downsucc_url(String[] inst_downsucc_url) {
		this.inst_downsucc_url = inst_downsucc_url;
	}

	public String[] getInst_installstart_url() {
		return inst_installstart_url;
	}

	public void setInst_installstart_url(String[] inst_installstart_url) {
		this.inst_installstart_url = inst_installstart_url;
	}

	public String[] getInst_installsucc_url() {
		return inst_installsucc_url;
	}

	public void setInst_installsucc_url(String[] inst_installsucc_url) {
		this.inst_installsucc_url = inst_installsucc_url;
	}

	public Integer getBatch_cnt() {
		return batch_cnt;
	}

	public void setBatch_cnt(Integer batch_cnt) {
		this.batch_cnt = batch_cnt;
	}

	public List<MaterialObject> getBatch_ma() {
		return batch_ma;
	}

	public void setBatch_ma(List<MaterialObject> batch_ma) {
		this.batch_ma = batch_ma;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getLanding_url() {
		return landing_url;
	}

	public void setLanding_url(String landing_url) {
		this.landing_url = landing_url;
	}

	public String getDeep_link() {
		return deep_link;
	}

	public void setDeep_link(String deep_link) {
		this.deep_link = deep_link;
	}

	public String[] getStart_url() {
		return start_url;
	}

	public void setStart_url(String[] start_url) {
		this.start_url = start_url;
	}

	public String[] getOver_url() {
		return over_url;
	}

	public void setOver_url(String[] over_url) {
		this.over_url = over_url;
	}

	public String getAd_source_mark() {
		return ad_source_mark;
	}

	public void setAd_source_mark(String ad_source_mark) {
		this.ad_source_mark = ad_source_mark;
	}

}
