package com.madhouse.media.xiaomi.request;

public class Imp {
	private String id;
	private String tagid;
	private Banner banner;
	private TextLink textlink;
	private Native nativead;
	private Video video;
	private Splash splash;
	private Integer instl = 0;
	private Integer admtype;
	private String templateid;
	private AdTemplate[] templates;
	private Integer pos;
	private double bidfloor = 0;
	private DirectDeal directdeal;
	private Integer adsCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public Banner getBanner() {
		return banner;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public void setBanner(Banner banner) {
		this.banner = banner;
	}

	public TextLink getTextlink() {
		return textlink;
	}

	public void setTextlink(TextLink textlink) {
		this.textlink = textlink;
	}

	public Native getNativead() {
		return nativead;
	}

	public void setNativead(Native nativead) {
		this.nativead = nativead;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public Splash getSplash() {
		return splash;
	}

	public void setSplash(Splash splash) {
		this.splash = splash;
	}

	public Integer getInstl() {
		return instl;
	}

	public void setInstl(Integer instl) {
		this.instl = instl;
	}

	public Integer getAdmtype() {
		return admtype;
	}

	public void setAdmtype(Integer admtype) {
		this.admtype = admtype;
	}

	public AdTemplate[] getTemplates() {
		return templates;
	}

	public void setTemplates(AdTemplate[] templates) {
		this.templates = templates;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public double getBidfloor() {
		return bidfloor;
	}

	public void setBidfloor(double bidfloor) {
		this.bidfloor = bidfloor;
	}

	public DirectDeal getDirectdeal() {
		return directdeal;
	}

	public void setDirectdeal(DirectDeal directdeal) {
		this.directdeal = directdeal;
	}

	public Integer getAdsCount() {
		return adsCount;
	}

	public void setAdsCount(Integer adsCount) {
		this.adsCount = adsCount;
	}

}
