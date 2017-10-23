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
		private List<Url> imptrackers;
		private List<Url> clicktrackers;
		private Url image;
		private Url video;
		private Url gif;

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

		public List<Url> getImptrackers() {
			return imptrackers;
		}

		public void setImptrackers(List<Url> imptrackers) {
			this.imptrackers = imptrackers;
		}

		public List<Url> getClicktrackers() {
			return clicktrackers;
		}

		public void setClicktrackers(List<Url> clicktrackers) {
			this.clicktrackers = clicktrackers;
		}

		public Url getImage() {
			return image;
		}

		public void setImage(Url image) {
			this.image = image;
		}

		public Url getVideo() {
			return video;
		}

		public void setVideo(Url video) {
			this.video = video;
		}

		public Url getGif() {
			return gif;
		}

		public void setGif(Url gif) {
			this.gif = gif;
		}

		public class Url {
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
