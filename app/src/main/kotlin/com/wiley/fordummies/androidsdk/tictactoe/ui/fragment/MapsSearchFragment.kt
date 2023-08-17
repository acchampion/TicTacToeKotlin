package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.search.SearchEngine
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillOptions
import com.mapbox.search.autofill.AddressAutofillResult
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber

/**
 * Created by acc in late July 2023.
 *
 * I modified this example based on <a href="https://docs.mapbox.com/android/search/examples/autofill-ui/">Mapbox's
 *   example app with address autofill and search</a>
 */

@Keep
class MapsSearchFragment : Fragment(), PermissionsListener,
	LocationEngineCallback<LocationEngineResult> {

	private val permissionsManager = PermissionsManager(this)

	private lateinit var addressAutofill: AddressAutofill
	private lateinit var searchEngine: SearchEngine

	private lateinit var searchResultsView: SearchResultsView
	private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter

	private lateinit var queryEditText: EditText

	private lateinit var apartmentEditText: EditText
	private lateinit var cityEditText: EditText
	private lateinit var stateEditText: EditText
	private lateinit var zipEditText: EditText
	private lateinit var fullAddress: TextView
	private lateinit var pinCorrectionNote: TextView
	private lateinit var mapView: MapView
	private lateinit var mapPin: View
	private lateinit var mapboxMap: MapboxMap
	private lateinit var locationEngine: LocationEngine

	private var ignoreNextMapIdleEvent: Boolean = false
	private var ignoreNextQueryTextUpdate: Boolean = false

	private val TAG = javaClass.simpleName


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		val v: View = inflater.inflate(R.layout.fragment_maps_search, container, false)

		queryEditText = v.findViewById(R.id.query_text)
		apartmentEditText = v.findViewById(R.id.address_apartment)
		cityEditText = v.findViewById(R.id.address_city)
		stateEditText = v.findViewById(R.id.address_state)
		zipEditText = v.findViewById(R.id.address_zip)
		fullAddress = v.findViewById(R.id.full_address)
		pinCorrectionNote = v.findViewById(R.id.pin_correction_note)

		mapPin = v.findViewById(R.id.map_pin)
		mapView = v.findViewById(R.id.map_search)
		mapboxMap = mapView.getMapboxMap()
		mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)

		mapboxMap.addOnMapIdleListener {
			if (ignoreNextMapIdleEvent) {
				ignoreNextMapIdleEvent = false
				return@addOnMapIdleListener
			}

			val mapCenter = mapboxMap.cameraState.center
			findAddress(mapCenter)
		}

		searchResultsView = v.findViewById(R.id.search_results_view)

		searchResultsView.initialize(
			SearchResultsView.Configuration(
				commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
			)
		)



		queryEditText.addTextChangedListener(object : TextWatcher {

			override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
				if (ignoreNextQueryTextUpdate) {
					ignoreNextQueryTextUpdate = false
					return
				}

				val query = Query.create(text.toString())
				if (query != null) {
					lifecycleScope.launchWhenStarted {
						addressAutofillUiAdapter.search(query)
					}
				}
				searchResultsView.isVisible = query != null
			}

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// Nothing to do
			}

			override fun afterTextChanged(s: Editable) {
				// Nothing to do
			}
		})

		return v
	}

	private fun setupMap(map: MapboxMap) {
		val cameraOptions = CameraOptions.Builder()
			.zoom(14.0)
			.build()
		map.setCamera(cameraOptions)
		map.loadStyleUri(Style.MAPBOX_STREETS)

	}


	override fun onResume() {
		super.onResume()

		val activity: Activity = requireActivity()

		if (!PermissionsManager.areLocationPermissionsGranted(activity)) {
			permissionsManager.requestLocationPermissions(activity)
			ActivityCompat.requestPermissions(
				activity, arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				), PERMISSIONS_REQUEST_LOCATION
			)
		} else {
			setupMap(mapView.getMapboxMap())
			initLocation()
		}

	}

	@SuppressLint("MissingPermission")
	private fun initLocation() {
		val context: Context = requireContext().applicationContext
		locationEngine = LocationEngineProvider.getBestLocationEngine(context)
		locationEngine.getLastLocation(this)

		addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))

		addressAutofillUiAdapter = AddressAutofillUiAdapter(
			view = searchResultsView, addressAutofill = addressAutofill
		)

		addressAutofillUiAdapter.addSearchListener(object :
			AddressAutofillUiAdapter.SearchListener {

			override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
				selectSuggestion(
					suggestion,
					fromReverseGeocoding = false,
				)
			}

			override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
				Timber.tag(TAG).d("SearchListener: onSuggestionsShown()")
			}

			override fun onError(e: Exception) {
				Timber.tag(TAG).d("SearchListener: onError()")
			}
		})
	}

	private fun findAddress(point: Point) {
		val ctx: Context = requireContext()
		lifecycleScope.launchWhenStarted {
			val response = addressAutofill.suggestions(point, AddressAutofillOptions())
			response.onValue { suggestions ->
				if (suggestions.isEmpty()) {
					Toast.makeText(
						ctx, R.string.address_autofill_error_pin_correction, Toast.LENGTH_SHORT
					).show();
				} else {
					selectSuggestion(
						suggestions.first(), fromReverseGeocoding = true
					)
				}
			}.onError {
				Toast.makeText(
					ctx, R.string.address_autofill_error_pin_correction, Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	private fun selectSuggestion(
		suggestion: AddressAutofillSuggestion, fromReverseGeocoding: Boolean
	) {
		val ctx: Context = requireContext()
		lifecycleScope.launchWhenStarted {
			val response = addressAutofill.select(suggestion)
			response.onValue { result ->
				showAddressAutofillResult(result, fromReverseGeocoding)
			}.onError {
				Toast.makeText(ctx, R.string.address_autofill_error_select, Toast.LENGTH_SHORT)
					.show()
			}
		}
	}

	private fun showAddressAutofillResult(
		result: AddressAutofillResult, fromReverseGeocoding: Boolean
	) {
		val address = result.address
		cityEditText.setText(address.place)
		stateEditText.setText(address.region)
		zipEditText.setText(address.postcode)

		fullAddress.isVisible = true
		fullAddress.text = result.suggestion.formattedAddress

		pinCorrectionNote.isVisible = true

		if (!fromReverseGeocoding) {
			mapView.getMapboxMap().setCamera(
				CameraOptions.Builder().center(result.suggestion.coordinate).zoom(16.0).build()
			)
			ignoreNextMapIdleEvent = true
			mapPin.isVisible = true
		}

		ignoreNextQueryTextUpdate = true
		queryEditText.setText(
			listOfNotNull(
				address.houseNumber, address.street
			).joinToString()
		)
		queryEditText.clearFocus()

		searchResultsView.isVisible = false
	}

	override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
		val ctx = requireContext()
		Timber.tag(TAG).d("onExplanationNeeded()")
		Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show()
	}

	override fun onPermissionResult(granted: Boolean) {
		val ctx = requireContext()
		if (granted) {
			setupMap(mapView.getMapboxMap())
			initLocation()
			Timber.tag(TAG).e("User granted location permission")
		} else {
			Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show()
			Timber.tag(TAG).d("User denied location permission")
		}
	}

	private companion object {
		const val PERMISSIONS_REQUEST_LOCATION = 0
	}

	@SuppressLint("MissingPermission")
	fun LocationEngine.lastKnownLocation(context: Context, callback: (Point?) -> Unit) {
		if (!PermissionsManager.areLocationPermissionsGranted(context)) {
			callback(null)
		}

		getLastLocation(object : LocationEngineCallback<LocationEngineResult> {
			override fun onSuccess(result: LocationEngineResult?) {
				val location = (result?.locations?.lastOrNull() ?: result?.lastLocation)?.let { location ->
					Point.fromLngLat(location.longitude, location.latitude)
				}
				callback(location)
			}

			override fun onFailure(exception: Exception) {
				callback(null)
			}
		})
	}


	override fun onSuccess(result: LocationEngineResult?) {
		if (result != null) {
			val location: android.location.Location? = result.lastLocation
			if (location != null) {
				val point: Point = Point.fromLngLat(location.longitude, location.latitude)
				mapView.getMapboxMap().setCamera(
					CameraOptions.Builder()
						.center(point)
						.zoom(14.0)
						.build())
				ignoreNextMapIdleEvent = true
			}
		}
	}

	override fun onFailure(exception: java.lang.Exception) {
		Timber.tag(TAG).e("Failed to get location")
	}
}
