package com.madhouse.media.momo;

import java.util.List;

public class MomoResponse {
	private String id;
	private List<Bid> bid;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Bid> getBid() {
		return bid;
	}

	public void setBid(List<Bid> bid) {
		this.bid = bid;
	}

	public class Bid {
		private String impid;
		private String crid;
		private String nurl;
		private String click_url;
		private List<String> imptrackers;
		private List<String> clicktrackers;
		private Image image;
		private Video video;
		private Gif gif;
		public String getImpid() {
			return impid;
		}
		public void setImpid(String impid) {
			this.impid = impid;
		}
		public String getCrid() {
			return crid;
		}
		public void setCrid(String crid) {
			this.crid = crid;
		}
		public String getNurl() {
			return nurl;
		}
		public void setNurl(String nurl) {
			this.nurl = nurl;
		}
		public String getClick_url() {
			return click_url;
		}
		public void setClick_url(String click_url) {
			this.click_url = click_url;
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
		public Image getImage() {
			return image;
		}
		public void setImage(Image image) {
			this.image = image;
		}
		public Video getVideo() {
			return video;
		}
		public void setVideo(Video video) {
			this.video = video;
		}
		public Gif getGif() {
			return gif;
		}
		public void setGif(Gif gif) {
			this.gif = gif;
		}
		
		public class Image {
			private String url;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
		}
		public class Gif {
			private String url;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
		}
		public class Video {
			private String url;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
		}
	}

}
