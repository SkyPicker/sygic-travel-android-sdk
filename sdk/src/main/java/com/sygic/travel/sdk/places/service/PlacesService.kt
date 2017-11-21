package com.sygic.travel.sdk.places.service

import com.sygic.travel.sdk.common.api.SygicTravelApiClient
import com.sygic.travel.sdk.places.model.Place
import com.sygic.travel.sdk.places.model.PlaceInfo
import com.sygic.travel.sdk.places.model.media.Medium
import com.sygic.travel.sdk.places.model.query.PlacesQuery

internal class PlacesService(
	private val sygicTravelApiClient: SygicTravelApiClient
) {
	fun getPlaces(
		placesQuery: PlacesQuery
	): List<PlaceInfo>? {
		val request = sygicTravelApiClient.getPlaces(
			placesQuery.query,
			placesQuery.levelsQueryString,
			placesQuery.categoriesQueryString,
			placesQuery.mapTilesQueryString,
			placesQuery.mapSpread,
			placesQuery.boundsQueryString,
			placesQuery.tagsQueryString,
			placesQuery.parentsQueryString,
			placesQuery.limit
		)
		val response = request.execute()
		return response.body()?.data?.getPlaces()
	}

	fun getPlaceDetailed(id: String): Place? {
		val request = sygicTravelApiClient.getPlaceDetailed(id)
		return request.execute().body()?.data?.getPlace()
	}

	fun getPlacesDetailed(ids: List<String>): List<Place>? {
		val queryIds = ids.joinToString(PlacesQuery.Operator.OR.operator)
		val request = sygicTravelApiClient.getPlacesDetailed(queryIds)
		return request.execute().body()?.data?.getPlaces()
	}

	fun getPlaceMedia(id: String): List<Medium>? {
		val request = sygicTravelApiClient.getPlaceMedia(id)
		return request.execute().body()?.data?.getMedia()
	}
}
