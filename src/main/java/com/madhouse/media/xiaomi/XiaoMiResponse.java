package com.madhouse.media.xiaomi;

public class XiaoMiResponse {
	private String id;
	private SeatBid[] seatbid;
	private String bidid;
	private Integer nbr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SeatBid[] getSeatbid() {
		return seatbid;
	}

	public void setSeatbid(SeatBid[] seatbid) {
		this.seatbid = seatbid;
	}

	public String getBidid() {
		return bidid;
	}

	public void setBidid(String bidid) {
		this.bidid = bidid;
	}

	public Integer getNbr() {
		return nbr;
	}

	public void setNbr(Integer nbr) {
		this.nbr = nbr;
	}

	public class SeatBid {
		private Bid[] bid;
		private String seat;
		private Integer cm = 0;
		private Integer group = 0;

		public Bid[] getBid() {
			return bid;
		}

		public void setBid(Bid[] bid) {
			this.bid = bid;
		}

		public String getSeat() {
			return seat;
		}

		public void setSeat(String seat) {
			this.seat = seat;
		}

		public Integer getCm() {
			return cm;
		}

		public void setCm(Integer cm) {
			this.cm = cm;
		}

		public Integer getGroup() {
			return group;
		}

		public void setGroup(Integer group) {
			this.group = group;
		}

	}

	public class Bid {
		private String id;
		private String impid;
		private double price;
		private String adid;
		private String nurl;
		private String adm;
		private String tagid;
		private String templateid;
		private int billingtype;
		private String[] adomain;
		private String bundle;
		private String cid;
		private String crid;
		private String[] cat;
		private String[] attr;
		private Integer h;
		private Integer w;
		private String landingurl;
		private String[] impurl;
		private String[] curl;
		private FrequencyCapping frequencycapping;
		private String extdata;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getImpid() {
			return impid;
		}

		public void setImpid(String impid) {
			this.impid = impid;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public String getAdid() {
			return adid;
		}

		public void setAdid(String adid) {
			this.adid = adid;
		}

		public String getNurl() {
			return nurl;
		}

		public void setNurl(String nurl) {
			this.nurl = nurl;
		}

		public String getAdm() {
			return adm;
		}

		public String getLandingurl() {
			return landingurl;
		}

		public void setLandingurl(String landingurl) {
			this.landingurl = landingurl;
		}

		public void setAdm(String adm) {
			this.adm = adm;
		}

		public String getTagid() {
			return tagid;
		}

		public void setTagid(String tagid) {
			this.tagid = tagid;
		}

		public String getTemplateid() {
			return templateid;
		}

		public void setTemplateid(String templateid) {
			this.templateid = templateid;
		}

		public int getBillingtype() {
			return billingtype;
		}

		public void setBillingtype(int billingtype) {
			this.billingtype = billingtype;
		}

		public String[] getAdomain() {
			return adomain;
		}

		public void setAdomain(String[] adomain) {
			this.adomain = adomain;
		}

		public String getBundle() {
			return bundle;
		}

		public void setBundle(String bundle) {
			this.bundle = bundle;
		}

		public String getCid() {
			return cid;
		}

		public void setCid(String cid) {
			this.cid = cid;
		}

		public String getCrid() {
			return crid;
		}

		public void setCrid(String crid) {
			this.crid = crid;
		}

		public String[] getCat() {
			return cat;
		}

		public void setCat(String[] cat) {
			this.cat = cat;
		}

		public String[] getAttr() {
			return attr;
		}

		public void setAttr(String[] attr) {
			this.attr = attr;
		}

		public Integer getH() {
			return h;
		}

		public void setH(Integer h) {
			this.h = h;
		}

		public Integer getW() {
			return w;
		}

		public void setW(Integer w) {
			this.w = w;
		}

		public String[] getImpurl() {
			return impurl;
		}

		public void setImpurl(String[] impurl) {
			this.impurl = impurl;
		}

		public String[] getCurl() {
			return curl;
		}

		public void setCurl(String[] curl) {
			this.curl = curl;
		}

		public FrequencyCapping getFrequencycapping() {
			return frequencycapping;
		}

		public void setFrequencycapping(FrequencyCapping frequencycapping) {
			this.frequencycapping = frequencycapping;
		}

		public String getExtdata() {
			return extdata;
		}

		public void setExtdata(String extdata) {
			this.extdata = extdata;
		}

	}

	public class FrequencyCapping {
		private Integer global = 1;
		private Integer weekly = -1;
		private Integer daily = -1;
		private Integer hourly = -1;

		public Integer getGlobal() {
			return global;
		}

		public void setGlobal(Integer global) {
			this.global = global;
		}

		public Integer getWeekly() {
			return weekly;
		}

		public void setWeekly(Integer weekly) {
			this.weekly = weekly;
		}

		public Integer getDaily() {
			return daily;
		}

		public void setDaily(Integer daily) {
			this.daily = daily;
		}

		public Integer getHourly() {
			return hourly;
		}

		public void setHourly(Integer hourly) {
			this.hourly = hourly;
		}

	}
}
