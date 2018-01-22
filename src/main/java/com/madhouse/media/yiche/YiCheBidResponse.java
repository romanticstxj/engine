package com.madhouse.media.yiche;

import java.util.List;

public class YiCheBidResponse {
	private String id;//竞价请求接口中的Bidrequest的ID，（交易平台会根据BidRequest.id进行校验，如校验失败，则放弃整个竟价请求的曝光机会）
	private List<SeatBid> seatbid;//seatbid竞价数组的对象。 （至少要有一个seat对象响应，其中至少包含一个对应的曝光机会）
	private String bidid;//DSP用来追踪/记录信息用的响应唯一ID，将uuid采用32位小写md5加密方式传输。（bidid一般用于日志记录投放响应的唯一标识，便于日后竟价请求的追踪）
	
	
	public static class SeatBid{
		private List<Bid> bid;//至少包含一个Bid对象的数组，每一个Bid对象都是针对每一次imp曝光机会的。（未来针对一次曝光机会可能有多个Bid）
		private String seat;//代表出价方的ID（如果不属于竟价交易订单，仅需要传输默认seat，用于表示默认价格标识，格式为32位小写md5）
		
		public static class Bid{
			private String id;//DSP为了信息追踪、记录生成的bid ID，将uuid采用32位小写md5加密方式传输。
			private String impid;//竞价请求接口中Imp对象的ID（交易平台会根据Bidrequest.impid进行校验，如校验失败，则放弃当前imp的曝光机会） 
			private String adid;//由DSP响应的物料托管ID，托管物料流程请参考 《易车网OSS服务》对接说明。（交易平台会根据订单、DSP、广告位、模版等多个维度来校验物料是否合规。如校验失败，则放弃当前imp的曝光机会）
			private String dealid;//交易编号，对应竟价请求接口中Deal对象ID（交易平台会根据Bidrequest.dealid进行校验。如校验失败，则放弃当前imp的曝光机会） durl string array required 曝光监测地址： 1.曝光监测地址支持多个（最多2个）； 2.曝光监测地址无需做urlencode功能，DSP需保证监测地址的响应质量，地址中不能包含特殊字符； 3.目前广告交易平台，暂不采用宏替换功能； 4.如在交易时曝光监测异常，DSP需及时反馈问题，配合处理；
			private List<String> durl;//曝光监测地址： 1.曝光监测地址支持多个（最多2个）； 2.曝光监测地址无需做urlencode功能，DSP需保证监测地址的响应质量，地址中不能包含特殊字符； 3.目前广告交易平台，暂不采用宏替换功能； 4.如在交易时曝光监测异常，DSP需及时反馈问题，配合处理
			private String curl;//点击监测地址：
			/*1.点击监测地址与跳转页，由DSP负责拼接。采用HTTP 302 方式实现；
			2.点击监测地址302跳转段数，不能超过2个，DSP需保证监测地址的响应质量，地址中不能包含特殊字符。监测地址第一段，不能做urlencode的操作；
			3.目前广告交易平台，暂不采用宏替换功能；
			4.如在交易时点击监测异常，DSP需及时反馈问题，配合处理；*/
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
			public String getAdid() {
				return adid;
			}
			public void setAdid(String adid) {
				this.adid = adid;
			}
			public String getDealid() {
				return dealid;
			}
			public void setDealid(String dealid) {
				this.dealid = dealid;
			}
			public String getCurl() {
				return curl;
			}
			public void setCurl(String curl) {
				this.curl = curl;
			}
			public List<String> getDurl() {
				return durl;
			}
			public void setDurl(List<String> durl) {
				this.durl = durl;
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
	

}
