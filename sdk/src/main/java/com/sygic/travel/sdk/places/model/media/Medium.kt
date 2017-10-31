package com.sygic.travel.sdk.places.model.media

import com.sygic.travel.sdk.places.model.geo.Location

/**
 * Place medium.
 */
class Medium {
	var id: String? = null
	var type: String? = null
	var urlTemplate: String? = null
	var url: String? = null
	var original: Original? = null
	var suitability: List<String>? = null
	var createdAt: String? = null
	var source: Source? = null
	var createdBy: String? = null
	var attribution: Attribution? = null
	var location: Location? = null
}
