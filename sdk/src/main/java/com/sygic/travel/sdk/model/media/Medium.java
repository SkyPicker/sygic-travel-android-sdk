package com.sygic.travel.sdk.model.media;

import com.google.gson.annotations.SerializedName;
import com.sygic.travel.sdk.model.geo.Location;

import java.util.List;

/**
 * <p>Place's medium.</p>
 */

public class Medium {
	private static final String ID = "id";
	private static final String TYPE = "type";
	private static final String URL_TEMPLATE = "url_template";
	private static final String URL = "url";
	private static final String CREATED_AT = "created_at";
	private static final String CREATED_BY = "created_by";
	private static final String LAT = "lat";
	private static final String LNG = "lng";
	private static final String LOCATION = "location";
	private static final String ORIGINAL = "original";
	private static final String ATTRIBUTION = "attribution";
	private static final String SOURCE = "source";
	private static final String SUITABILITY = "suitability";

	@SerializedName(ID)
	private String id;

	@SerializedName(TYPE)
	private String type;

	@SerializedName(URL_TEMPLATE)
	private String urlTemplate;

	@SerializedName(URL)
	private String url;

	@SerializedName(CREATED_AT)
	private String createdAt;
	
	@SerializedName(CREATED_BY)
	private String createdBy;
	
	@SerializedName(LAT)
	private float lat;
	
	@SerializedName(LNG)
	private float lng;
	
	@SerializedName(LOCATION)
	private Location location;
	
	@SerializedName(ORIGINAL)
	private Original original;
	
	@SerializedName(ATTRIBUTION)
	private Attribution attribution;
	
	@SerializedName(SOURCE)
	private Source source;
	
	@SerializedName(SUITABILITY)
	private List<String> suitability = null;

	public Medium() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Object getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public Object getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public Object getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Original getOriginal() {
		return original;
	}

	public void setOriginal(Original original) {
		this.original = original;
	}

	public Attribution getAttribution() {
		return attribution;
	}

	public void setAttribution(Attribution attribution) {
		this.attribution = attribution;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public List<String> getSuitability() {
		return suitability;
	}

	public void setSuitability(List<String> suitability) {
		this.suitability = suitability;
	}
}
