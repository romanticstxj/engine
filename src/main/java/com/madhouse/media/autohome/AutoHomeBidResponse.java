package com.madhouse.media.autohome;

import java.util.List;

public class AutoHomeBidResponse{
	
	private String id;//竞价ID
	private String version;//版本号，需要与请求保持一致
	private Integer processing_time_ms;//dsp处理时间单位毫秒
	private boolean is_cm;//是否 cookie mapping ( app 端无 效）true 是，false 否
	private List<Ads> ads;//广告
	
	public static class Ads{
		private String id;//曝光id同请求
		private String slotid;//广告位id
		private int max_cpm_price;//最高出价（PDB PD请直接返回  底价）
		private Long creative_id;//素材id  需要使用素材审核时返回的素  材id，否则会校验失败
		private Integer advertiser_id;//广告主id
		private int width;//素材宽(如果是文字链暂不填）
		private int height;//素材高(如果是文字链暂不填）
		private Integer category;//素材所属行业
		private Integer creative_type;//素材类型(字典定义）
		private int templateId;//从请求中携带的模板id列表中  选择一个模板id
		private Adsnippet adsnippet;//广告片段
		
		public static class Adsnippet{
			private String img;//素材url地址
			private List<String> pv;//曝光监控地址
			private String link;//点击地址，ADX负责302跳转  到该地址，如果第二跳不是落  地页则第二跳地址需要负责继  续跳转到落地页（该字段需要 与点击宏结合使用）
			private List<Content> content;
			
			public static class Content{
				private String src;
				private String type;
				public String getSrc() {
					return src;
				}
				public void setSrc(String src) {
					this.src = src;
				}
				public String getType() {
					return type;
				}
				public void setType(String type) {
					this.type = type;
				}
				
			}
			
			public String getImg() {
				return img;
			}
			public void setImg(String img) {
				this.img = img;
			}
			public List<String> getPv() {
				return pv;
			}
			public void setPv(List<String> pv) {
				this.pv = pv;
			}
			public String getLink() {
				return link;
			}
			public void setLink(String link) {
				this.link = link;
			}
			public List<Content> getContent() {
				return content;
			}
			public void setContent(List<Content> content) {
				this.content = content;
			}
			
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSlotid() {
			return slotid;
		}

		public void setSlotid(String slotid) {
			this.slotid = slotid;
		}

		public int getMax_cpm_price() {
			return max_cpm_price;
		}

		public void setMax_cpm_price(int max_cpm_price) {
			this.max_cpm_price = max_cpm_price;
		}

		public Long getCreative_id() {
			return creative_id;
		}

		public void setCreative_id(Long creative_id) {
			this.creative_id = creative_id;
		}

		public Integer getAdvertiser_id() {
			return advertiser_id;
		}

		public void setAdvertiser_id(Integer advertiser_id) {
			this.advertiser_id = advertiser_id;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public Integer getCategory() {
			return category;
		}

		public void setCategory(Integer category) {
			this.category = category;
		}

		public Integer getCreative_type() {
			return creative_type;
		}

		public void setCreative_type(Integer creative_type) {
			this.creative_type = creative_type;
		}

		public int getTemplateId() {
			return templateId;
		}

		public void setTemplateId(int templateId) {
			this.templateId = templateId;
		}

		public Adsnippet getAdsnippet() {
			return adsnippet;
		}

		public void setAdsnippet(Adsnippet adsnippet) {
			this.adsnippet = adsnippet;
		}
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getProcessing_time_ms() {
		return processing_time_ms;
	}

	public void setProcessing_time_ms(Integer processing_time_ms) {
		this.processing_time_ms = processing_time_ms;
	}

	public boolean isIs_cm() {
		return is_cm;
	}

	public void setIs_cm(boolean is_cm) {
		this.is_cm = is_cm;
	}

	public List<Ads> getAds() {
		return ads;
	}

	public void setAds(List<Ads> ads) {
		this.ads = ads;
	}
	
}
