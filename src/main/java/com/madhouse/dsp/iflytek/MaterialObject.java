package com.madhouse.dsp.iflytek;
/**
 * batch_ma Material object
 * */
public class MaterialObject {
	//（普通广告信息（ json 格式））
	private String adtype;//广告交互类型
	private String package_name;//下载包名称
	private String title;//title of AD
	private String sub_title;//字幕
	private String click_text;//点击说明，插屏图文存在该属性  如：了解详情，点击下载
	private String right_icon_url;//插屏图文存在该属性
	private String w;//宽
	private String h;//高
	private String image;//image url
	private String icon;//icon url
	private String landing_url;//
	private String deep_link;
	private String[] impr_url;//曝光监控数组，
	private String[] click_url;//点击监控数组
	private String[] inst_downstart_url;//针对下载类广告， 下载开始上报监控
	private String[] inst_downsucc_url;//针对下载类广告， 下载成功上报监控
	private String[] inst_installstart_url;//针对下载类广告， 安装开始上报监控
	private String[] inst_installsucc_url;//针对下载类广告， 安装成功上报监控
	private String ad_source_mark;//广告来源
	
	//原生广告
	private String[] img_urls;//用于新增的一图和三图一文广告类型，每一项表示一个图片链接，该数组有几项就是几个图。
	
	
	
	
	public String[] getImg_urls() {
		return img_urls;
	}
	public void setImg_urls(String[] img_urls) {
		this.img_urls = img_urls;
	}
	public String getAdtype() {
		return adtype;
	}
	public void setAdtype(String adtype) {
		this.adtype = adtype;
	}
	public String getPackage_name() {
		return package_name;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSub_title() {
		return sub_title;
	}
	public void setSub_title(String sub_title) {
		this.sub_title = sub_title;
	}
	public String getClick_text() {
		return click_text;
	}
	public void setClick_text(String click_text) {
		this.click_text = click_text;
	}
	public String getRight_icon_url() {
		return right_icon_url;
	}
	public void setRight_icon_url(String right_icon_url) {
		this.right_icon_url = right_icon_url;
	}
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	public String getH() {
		return h;
	}
	public void setH(String h) {
		this.h = h;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
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
	public String getAd_source_mark() {
		return ad_source_mark;
	}
	public void setAd_source_mark(String ad_source_mark) {
		this.ad_source_mark = ad_source_mark;
	}
	
	
	
}
