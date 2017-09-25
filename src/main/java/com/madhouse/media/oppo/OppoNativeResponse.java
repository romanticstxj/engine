package com.madhouse.media.oppo;

import java.util.List;

import com.madhouse.media.oppo.OppoNativeResponse.Asset.Link;

public class OppoNativeResponse {
		private String ver;
		private List<Asset> assets;
		private Link lint;
		private List<String> imptrackers;
		private String jstracker;
		private Link link;
		public class Link{
			private String url;
			private List<String> clicktrackers;
			private String fallback;
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public List<String> getClicktrackers() {
				return clicktrackers;
			}
			public void setClicktrackers(List<String> clicktrackers) {
				this.clicktrackers = clicktrackers;
			}
			public String getFallback() {
				return fallback;
			}
			public void setFallback(String fallback) {
				this.fallback = fallback;
			}
		}
		public class Asset{
			private int id;
			private int required;
			private Title title;
			private Img img;
			private Video video;
			private Link link;
			private Data data;
			private SpecificFeeds specificFeeds;
			
			public class Title{
				private String text;
				public String getText() {
					return text;
				}
				public void setText(String text) {
					this.text = text;
				}
			}
			public class Img{
				private String url;
				private Integer w;
				private Integer h;
				public String getUrl() {
					return url;
				}
				public void setUrl(String url) {
					this.url = url;
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
			}
			public class Video{
				
			}
			public class Link{
				private String url;
				private List<String> clicktrackers;
				private String fallback;
				public String getUrl() {
					return url;
				}
				public void setUrl(String url) {
					this.url = url;
				}
				public List<String> getClicktrackers() {
					return clicktrackers;
				}
				public void setClicktrackers(List<String> clicktrackers) {
					this.clicktrackers = clicktrackers;
				}
				public String getFallback() {
					return fallback;
				}
				public void setFallback(String fallback) {
					this.fallback = fallback;
				}
			}
			public class Data{
				private String value;

				public String getValue() {
					return value;
				}
				public void setValue(String value) {
					this.value = value;
				}
			}
			public class SpecificFeeds{
				private int formateType;
				private List<String> imageUrls;
				public int getFormateType() {
					return formateType;
				}
				public void setFormateType(int formateType) {
					this.formateType = formateType;
				}
				public List<String> getImageUrls() {
					return imageUrls;
				}
				public void setImageUrls(List<String> imageUrls) {
					this.imageUrls = imageUrls;
				}
			}
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			public int getRequired() {
				return required;
			}
			public void setRequired(int required) {
				this.required = required;
			}
			public Title getTitle() {
				return title;
			}
			public void setTitle(Title title) {
				this.title = title;
			}
			public Img getImg() {
				return img;
			}
			public void setImg(Img img) {
				this.img = img;
			}
			public Video getVideo() {
				return video;
			}
			public void setVideo(Video video) {
				this.video = video;
			}
			public Link getLink() {
				return link;
			}
			public void setLink(Link link) {
				this.link = link;
			}
			public Data getData() {
				return data;
			}
			public void setData(Data data) {
				this.data = data;
			}
			public SpecificFeeds getSpecificFeeds() {
				return specificFeeds;
			}
			public void setSpecificFeeds(SpecificFeeds specificFeeds) {
				this.specificFeeds = specificFeeds;
			}
			
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}
		public List<Asset> getAssets() {
			return assets;
		}
		public void setAssets(List<Asset> assets) {
			this.assets = assets;
		}
		public Link getLint() {
			return lint;
		}
		public void setLint(Link lint) {
			this.lint = lint;
		}
		public List<String> getImptrackers() {
			return imptrackers;
		}
		public void setImptrackers(List<String> imptrackers) {
			this.imptrackers = imptrackers;
		}
		public String getJstracker() {
			return jstracker;
		}
		public void setJstracker(String jstracker) {
			this.jstracker = jstracker;
		}
		public Link getLink() {
			return link;
		}
		public void setLink(Link link) {
			this.link = link;
		}
}
