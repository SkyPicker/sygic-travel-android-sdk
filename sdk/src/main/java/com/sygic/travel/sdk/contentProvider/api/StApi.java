package com.sygic.travel.sdk.contentProvider.api;

import com.sygic.travel.sdk.model.StResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.sygic.travel.sdk.contentProvider.api.ApiConstants.CONTENT_TYPE_JSON;

public interface StApi {

	String PLACES_API_CALL = "places_api_call";
	String DETAIL_API_CALL = "detail_api_call";

	/*******************************************************************************/
	/*                                     GET                                     */
	/*******************************************************************************/

	@Headers(CONTENT_TYPE_JSON)
	@GET("places")
	Call<StResponse> getPlaces(
		@Query("query") String query,
		@Query("level") String level,
		@Query("categories") String categories,
		@Query("map_tile") String mapTile,
		@Query("map_spread") Integer mapSpread,
		@Query("bounds") String bounds,
		@Query("tags") String tags,
		@Query("parent") String parent,
		@Query("limit") Integer limit
	);

	@Headers(CONTENT_TYPE_JSON)
	@GET("place-details/{place_guid}")
	Call<StResponse> getPlaceDetailed(
		@Path("place_guid") String placeGuid
	);

	@Headers(CONTENT_TYPE_JSON)
	@GET("places/{place_guid}/media")
	Call<StResponse> getPlaceMedia(
		@Path("place_guid") String placeGuid
	);
}
