package com.sygic.travel.sdk.model.api;

import com.google.gson.annotations.SerializedName;
import com.sygic.travel.sdk.model.media.Medium;

import java.util.List;

/**
 * <p>Response that contains list of place's media. Suitable for a gallery.</p>
 */
public class MediaResponse extends StResponse {
	private Data data;

	public Data getData() {
		return data;
	}

	/**
	 * Contains media data from an API response.
	 */
	public class Data {
		static final String MEDIA = "media";

		@SerializedName(MEDIA)
		private List<Medium> media;

		public List<Medium> getMedia() {
			return media;
		}
	}
}
