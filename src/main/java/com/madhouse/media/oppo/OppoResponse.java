package com.madhouse.media.oppo;

import java.util.List;


public class OppoResponse {
   private String id;//竞价请求id
   private List<SeatBid> seatbid;
   private String bidid;//竞价者生成的响应唯一id
   private Integer nbr;
   public class SeatBid{
	   private List<Bid> bid;
	   private String seat;
	   public class Bid{
		   private String id;//必填。竞价方生成的竞价唯一id
		   private String impid;//竞价请求中的imp的id
		   private long price;
		   private String nurl;//竞价成功后的url
		   private String burl;//计费回调的url
		   private String lurl;//竞价失败回调的url
		   private String adm;//广告标记。如果adm中包含成功回调地址，可以替换win notice
		   private String adid;//必填。
		   private String crid;//创意id
		   private String dealid;//与请求中的deal.id相同
		   private Integer exp;//竞价者等待拍卖到正式曝光的参考时间（单位s）
		   private List<String> imptrackers;//最多支持三个。原生类型不使用该字段
		   private List<String> clicktrackers;
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
		public long getPrice() {
			return price;
		}
		public void setPrice(long price) {
			this.price = price;
		}
		public String getNurl() {
			return nurl;
		}
		public void setNurl(String nurl) {
			this.nurl = nurl;
		}
		public String getBurl() {
			return burl;
		}
		public void setBurl(String burl) {
			this.burl = burl;
		}
		public String getLurl() {
			return lurl;
		}
		public void setLurl(String lurl) {
			this.lurl = lurl;
		}
		public String getAdm() {
			return adm;
		}
		public void setAdm(String adm) {
			this.adm = adm;
		}
		public String getAdid() {
			return adid;
		}
		public void setAdid(String adid) {
			this.adid = adid;
		}
		public String getCrid() {
			return crid;
		}
		public void setCrid(String crid) {
			this.crid = crid;
		}
		public String getDealid() {
			return dealid;
		}
		public void setDealid(String dealid) {
			this.dealid = dealid;
		}
		public Integer getExp() {
			return exp;
		}
		public void setExp(Integer exp) {
			this.exp = exp;
		}
		public List<String> getImptrackers() {
			return imptrackers;
		}
		public void setImptrackers(List<String> imptrackers) {
			this.imptrackers = imptrackers;
		}
		public List<String> getClicktrackers() {
			return clicktrackers;
		}
		public void setClicktrackers(List<String> clicktrackers) {
			this.clicktrackers = clicktrackers;
		}
		   
	   }
	public List<Bid> getBid() {
		return bid;
	}
	public void setBid(List<Bid> bid) {
		this.bid = bid;
	}
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
   }
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public List<SeatBid> getSeatbid() {
	return seatbid;
}
public void setSeatbid(List<SeatBid> seatbid) {
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
   
}
