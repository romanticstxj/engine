package com.madhouse.media.oppo;

import java.util.List;

public class OppoNativeRequest{
	private String ver;
	private List<Asset> assets;
	
	 class Asset{
		private int id;//必填。
		private int required;
		private Title title;//标题
		private Img img;//图片
		private SpecificFeeds specificFeeds;//特定的信息流对象，提供OPPO信息流广告使用
		private Data data;
		public class Title{
			private String len;//必填，标题元素长度最大值
			public String getLen() {
				return len;
			}
			public void setLen(String len) {
				this.len = len;
			}
		}
		public class Img{
			private Integer w;//宽
			private Integer wmin;//最小宽
			private Integer h;//高
			private Integer hmin;//最小高
			private List<String> mimes;
			public Integer getW() {
				return w;
			}
			public void setW(Integer w) {
				this.w = w;
			}
			public Integer getWmin() {
				return wmin;
			}
			public void setWmin(Integer wmin) {
				this.wmin = wmin;
			}
			public Integer getH() {
				return h;
			}
			public void setH(Integer h) {
				this.h = h;
			}
			public Integer getHmin() {
				return hmin;
			}
			public void setHmin(Integer hmin) {
				this.hmin = hmin;
			}
			public List<String> getMimes() {
				return mimes;
			}
			public void setMimes(List<String> mimes) {
				this.mimes = mimes;
			}
		}
		public class Data{
			private Integer type;//必填。 支持数据元素类型
			private Integer len;
			public Integer getType() {
				return type;
			}
			public void setType(Integer type) {
				this.type = type;
			}
			public Integer getLen() {
				return len;
			}
			public void setLen(Integer len) {
				this.len = len;
			}
		}
		public class SpecificFeeds{
			private List<Integer> formatTypes;//必填。支持对重素材规格，在响应中只需要返回其中一种规格

			public List<Integer> getFormatTypes() {
				return formatTypes;
			}
			public void setFormatTypes(List<Integer> formatTypes) {
				this.formatTypes = formatTypes;
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
		public SpecificFeeds getSpecificFeeds() {
			return specificFeeds;
		}
		public void setSpecificFeeds(SpecificFeeds specificFeeds) {
			this.specificFeeds = specificFeeds;
		}
		public Data getData() {
			return data;
		}
		public void setData(Data data) {
			this.data = data;
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
}
