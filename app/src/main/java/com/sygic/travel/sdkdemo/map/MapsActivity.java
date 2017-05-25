package com.sygic.travel.sdkdemo.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sygic.travel.sdk.StSDK;
import com.sygic.travel.sdk.contentProvider.api.Callback;
import com.sygic.travel.sdk.geo.quadkey.QuadkeysGenerator;
import com.sygic.travel.sdk.geo.spread.CanvasSize;
import com.sygic.travel.sdk.geo.spread.SpreadResult;
import com.sygic.travel.sdk.geo.spread.SpreadSizeConfig;
import com.sygic.travel.sdk.geo.spread.SpreadedPlace;
import com.sygic.travel.sdk.geo.spread.Spreader;
import com.sygic.travel.sdk.model.geo.Bounds;
import com.sygic.travel.sdk.model.place.Place;
import com.sygic.travel.sdk.model.query.Query;
import com.sygic.travel.sdkdemo.PlaceDetailActivity;
import com.sygic.travel.sdkdemo.R;
import com.sygic.travel.sdkdemo.filters.CategoriesAdapter;
import com.sygic.travel.sdkdemo.filters.CategoriesDialog;
import com.sygic.travel.sdkdemo.utils.MarkerBitmapGenerator;
import com.sygic.travel.sdkdemo.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity	implements OnMapReadyCallback {
	private static final String TAG = "SdkDemoApp-MapActivity";
	public static final String ID = "id";
	private static final double BOUNDS_OFFSET = 0.05;

	private double canvasWidthRatio, canvasHeightRatio;

	private GoogleMap map;
	private Spreader spreader;

	private CategoriesDialog categoriesDialog;
	private List<String> selectedCateoriesKeys = new ArrayList<>();
	private String titlePattern;

	private Callback<List<Place>> placesCallback;
	private View vMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		vMain = findViewById(R.id.ll_main);
		spreader = new Spreader(getResources());
		categoriesDialog = new CategoriesDialog(this, getOnCategoriesClick());
		titlePattern = getString(R.string.title_activity_maps) + " - %s";

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
			.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Observables need to be unsubscribed, when the activity comes to background
		StSDK.getInstance().unsubscribeObservable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// There's only one option in the menu - categories filter.
		getMenuInflater().inflate(R.menu.places, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_filter_places) {
			// Show dialog with categories
			categoriesDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	// Google maps specific method called, when the map is initialiazed and ready.
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		placesCallback = getPlacesCallback();

		// Center map to London
		LatLng londonLatLng = new LatLng(51.5116983, -0.1205079);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(londonLatLng, 14));

		// Set on marker's info window (bubble) click listener - starts place's detail activity.
		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				String id = ((String) marker.getTag());
				Intent placeDetailIntent = new Intent(MapsActivity.this, PlaceDetailActivity.class);
				placeDetailIntent.putExtra(ID, id);
				startActivity(placeDetailIntent);
			}
		});

		calculateCanvasSizeRatios();
		loadPlaces();
	}

	// This method is used only for the purposes of this sample. No ratios should be used in normal app.
	private void calculateCanvasSizeRatios() {
		LatLngBounds originalBounds = map.getProjection().getVisibleRegion().latLngBounds;
		LatLngBounds offsetBounds = new LatLngBounds(
			new LatLng(originalBounds.southwest.latitude - BOUNDS_OFFSET, originalBounds.southwest.longitude - BOUNDS_OFFSET),  //sw
			new LatLng(originalBounds.northeast.latitude + BOUNDS_OFFSET, originalBounds.northeast.longitude + BOUNDS_OFFSET)  //ne
		);

		final double dOffLat = offsetBounds.northeast.latitude - offsetBounds.southwest.latitude;
		final double dOrgLat = originalBounds.northeast.latitude - originalBounds.southwest.latitude;
		final double dOffLng = Math.max(Math.abs(offsetBounds.northeast.longitude), Math.abs(offsetBounds.southwest.longitude)) -
			Math.min(Math.abs(offsetBounds.northeast.longitude), Math.abs(offsetBounds.southwest.longitude));
		final double dOrgLng = Math.max(Math.abs(originalBounds.northeast.longitude), Math.abs(originalBounds.southwest.longitude)) -
			Math.min(Math.abs(originalBounds.northeast.longitude), Math.abs(originalBounds.southwest.longitude));
		canvasHeightRatio = dOffLat / dOrgLat;
		canvasWidthRatio = dOffLng / dOrgLng;
	}

	// Use the SDK to load places
	private void loadPlaces(){
		// Generate quadkeys from the map's bondings and zoom
		List<String> quadkeys = QuadkeysGenerator.generateQuadkeys(
			getMapBounds(),
			(int) map.getCameraPosition().zoom
		);

		Query query = new Query();
		query.setLevels(Collections.singletonList("poi"));
		query.setCategories(selectedCateoriesKeys);
		query.setMapTiles(quadkeys);
		query.setMapSpread(1);
		query.setBounds(getMapBounds());
		query.setParents(Collections.singletonList("city:1"));
		query.setLimit(32);
		StSDK.getInstance().getPlaces(query, placesCallback);
	}

	// On category click listener
	private CategoriesAdapter.ViewHolder.CategoryClick getOnCategoriesClick() {
		return new CategoriesAdapter.ViewHolder.CategoryClick() {
			@Override
			public void onCategoryClick(String categoryKey, String categoryName) {
				if(selectedCateoriesKeys.contains(categoryKey)){
					categoriesDialog.dismiss();
					return;
				}

				// Set activity's title
				if(categoryKey.equals("all")){
					selectedCateoriesKeys.clear();
					setTitle(getString(R.string.title_activity_maps));
				} else {
					selectedCateoriesKeys.add(categoryKey);
					setTitle(String.format(titlePattern, categoryName));
				}

				// Reload places
				loadPlaces();
				categoriesDialog.dismiss();
			}
		};
	}

	// Returns map's Bounds
	private Bounds getMapBounds() {
		LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
		Bounds bounds = new Bounds();

		// Map bounds are widened for the purposes of this sample. In a real app bounds without
		// the BOUNDS_OFFSET should be used.
		bounds.setSouth((float) (latLngBounds.southwest.latitude - BOUNDS_OFFSET));
		bounds.setWest((float) (latLngBounds.southwest.longitude - BOUNDS_OFFSET));
		bounds.setNorth((float) (latLngBounds.northeast.latitude + BOUNDS_OFFSET));
		bounds.setEast((float) (latLngBounds.northeast.longitude + BOUNDS_OFFSET));

		return bounds;
	}

	private void showPlacesOnMap(List<Place> places) {
		Bounds bounds = getMapBounds();

		map.clear();

		// Spread loaded places
		SpreadResult spreadResult = spreader.spreadPlacesOnMap(
			places,
			bounds,
			// Ratios are used only for purposes of this sample, no ratios should be used in normal app.
			new CanvasSize(
				(int) (vMain.getMeasuredWidth() * canvasWidthRatio),
				(int) (vMain.getMeasuredHeight() * canvasHeightRatio)
			)
		);

		// Create markers for spread places
		for(SpreadedPlace spreadedPlace : spreadResult.getVisiblePlaces()) {
			Place place = spreadedPlace.getPlace();
			Marker newMarker = map.addMarker(new MarkerOptions()
				.position(new LatLng(place.getLocation().getLat(), place.getLocation().getLng()))
				.title(place.getName())
				.snippet(place.getPerex())
				.icon(getMarkerBitmapDescriptor(spreadedPlace))
			);
			newMarker.setTag(place.getId());
		}
	}

	// This method returns bitmap descriptor, which is used for Google maps specifically
	private BitmapDescriptor getMarkerBitmapDescriptor(SpreadedPlace spreadedPlace) {
		try {
			// create marker's custom bitmap descriptor, if possible
			Bitmap markerBitmap = MarkerBitmapGenerator.createMarkerBitmap(this, spreadedPlace);
			if(markerBitmap != null) {
				return BitmapDescriptorFactory.fromBitmap(markerBitmap);
			} else {
				return null;
			}
		} catch(Exception e) {
			// If anything goes wrong, Google maps default pin with specific hue is returned.
			return BitmapDescriptorFactory.defaultMarker(getMarkerHue(spreadedPlace.getPlace()));
		}
	}

	// Return hue for Google maps default pin
	private float getMarkerHue(Place place) {
		float markerHue = BitmapDescriptorFactory.HUE_RED;
		if(place.getCategories() != null && place.getCategories().size() > 0){
			markerHue = Utils.getMarkerHue(place.getCategories().get(0));
		}
		return markerHue;
	}

	private Callback<List<Place>> getPlacesCallback() {
		if(placesCallback == null){
			placesCallback = new Callback<List<Place>>() {
				@Override
				public void onSuccess(List<Place> places) {
					showPlacesOnMap(places);
				}

				@Override
				public void onFailure(Throwable t) {
					Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
					t.printStackTrace();
				}
			};
		}
		return placesCallback;
	}
}
