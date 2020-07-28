package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Item {
	private Set<String> keywords;
	private String name;
	private String url;
	private String itemId;
	private String imageUrl;
	private String address;
	private Item(ItemBuilder builder) {
		this.keywords = builder.keywords;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.address = builder.address;
	}
	//create item builder
	public static ItemBuilder builder() {
		return new ItemBuilder();
	}
	//convert item to JSONObject
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("item_id", this.itemId);
		obj.put("name", this.name);
		obj.put("url", this.url);
		obj.put("keywords", new JSONArray(this.keywords));
		obj.put("image_url", this.imageUrl);
		obj.put("address", this.address);
		return obj;
	}
	//getter
	public Set<String> getKeywords() {
		return keywords;
	}
	public String getTiltle() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getItemId() {
		return itemId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getAddress() {
		return address;
	}
	
	//builder pattern
	public static class ItemBuilder {
		private Set<String> keywords;
		private String name;
		private String url;
		private String itemId;
		private String imageUrl;
		private String address;
		
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}
		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		public ItemBuilder setKeywords(Set<String> keywords) {
			this.keywords = keywords;
			return this;
		}
		public Item build() {			
			return new Item(this);
		}
		
	}
	
}
