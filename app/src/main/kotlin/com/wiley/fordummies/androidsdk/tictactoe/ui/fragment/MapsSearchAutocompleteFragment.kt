package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteOptions
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.autocomplete.PlaceAutocompleteType
import com.mapbox.search.result.SearchAddress
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.SearchResultsView
import com.mapbox.search.ui.view.place.SearchPlace
import com.mapbox.search.ui.view.place.SearchPlaceBottomSheetView
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber

/**
 * Created by acc in January 2025. Based on the PlaceAutocompleteActivity in Mapbox's sample
 * search app.
 */
class MapsSearchAutocompleteFragment : Fragment(), PermissionsListener {

    private lateinit var placeAutocomplete: PlaceAutocomplete

    private lateinit var searchResultsView: SearchResultsView
    private lateinit var placeAutocompleteUiAdapter: PlaceAutocompleteUiAdapter

    private lateinit var queryEditText: EditText

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapMarkersManager: MapMarkersManager

    private lateinit var searchPlaceView: SearchPlaceBottomSheetView

    private var ignoreNextQueryUpdate = false

    private val mActivityResult = registerForActivityResult(
        RequestPermission()
    ) { result ->
        if (result) {
            // We have permission, so show the contacts.
            showLocation()
        } else {
            // The user denied permission to read contacts, so show them a message.
            Timber.e("Error: Permission denied to read contacts")
            if (lacksLocationPermission()) {
                val activity = requireActivity() as AppCompatActivity
                val fm = activity.supportFragmentManager
                Toast.makeText(requireContext(), "User denied location permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_maps_search_auto, container, false)

        getString(R.string.mapbox_access_token)

        queryEditText = v.findViewById(R.id.query_text)

        mapView = v.findViewById(R.id.map_view)
        mapView.mapboxMap.also { mapboxMap ->
            this.mapboxMap = mapboxMap

            mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
                mapView.location.updateSettings {
                    enabled = true
                }

                mapView.location.addOnIndicatorPositionChangedListener(object :
                    OnIndicatorPositionChangedListener {
                    override fun onIndicatorPositionChanged(point: Point) {
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(point)
                                .zoom(14.0)
                                .build()
                        )

                        mapView.location.removeOnIndicatorPositionChangedListener(this)
                    }
                })
            }
        }

        mapMarkersManager = MapMarkersManager(mapView)

        mapboxMap.addOnMapLongClickListener {
            reverseGeocoding(it)
            return@addOnMapLongClickListener true
        }

        searchResultsView = v.findViewById(R.id.search_results_view)

        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration()
            )
        )

        placeAutocompleteUiAdapter = PlaceAutocompleteUiAdapter(
            view = searchResultsView,
            placeAutocomplete = placeAutocomplete
        )

        searchPlaceView = v.findViewById<SearchPlaceBottomSheetView>(R.id.search_place_view).apply {
            initialize(CommonSearchViewConfiguration())

            isFavoriteButtonVisible = false

            addOnCloseClickListener {
                hide()
                closePlaceCard()
            }

            addOnNavigateClickListener { searchPlace ->

                startActivity(
                    geoIntent(searchPlace.coordinate)
                )
            }

            addOnShareClickListener { searchPlace ->
                startActivity(shareIntent(searchPlace))
            }
        }

        if (lacksLocationPermission()) {
            mActivityResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        placeAutocompleteUiAdapter.addSearchListener(object :
            PlaceAutocompleteUiAdapter.SearchListener {

            override fun onSuggestionsShown(suggestions: List<PlaceAutocompleteSuggestion>) {
                // Nothing to do
            }

            override fun onSuggestionSelected(suggestion: PlaceAutocompleteSuggestion) {
                openPlaceCard(suggestion)
            }

            override fun onPopulateQueryClick(suggestion: PlaceAutocompleteSuggestion) {
                queryEditText.setText(suggestion.name)
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })

        queryEditText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false
                } else {
                    closePlaceCard()
                }

                lifecycleScope.launchWhenStarted {
                    placeAutocompleteUiAdapter.search(text.toString())
                    searchResultsView.isVisible = text.isNotEmpty()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })

        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
        }

        return v
    }

    private fun reverseGeocoding(point: Point) {
        val types: List<PlaceAutocompleteType> = when (mapboxMap.cameraState.zoom) {
            in 0.0..4.0 -> REGION_LEVEL_TYPES
            in 4.0..6.0 -> DISTRICT_LEVEL_TYPES
            in 6.0..12.0 -> LOCALITY_LEVEL_TYPES
            else -> ALL_TYPES
        }

        lifecycleScope.launchWhenStarted {
            val response = placeAutocomplete.reverse(point, PlaceAutocompleteOptions(types = types))
            response.onValue { suggestions ->
                if (suggestions.isEmpty()) {
                    showToast(R.string.place_autocomplete_reverse_geocoding_error_message)
                } else {
                    openPlaceCard(suggestions.first())
                }
            }.onError { error ->
                Timber.d(LOG_TAG, "Reverse geocoding error", error)
                showToast(R.string.place_autocomplete_reverse_geocoding_error_message)
            }
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }

    private class MapMarkersManager(mapView: MapView) {

        private val mapboxMap = mapView.mapboxMap
        private val circleAnnotationManager =
            mapView.annotations.createCircleAnnotationManager(null)
        private val markers = mutableMapOf<String, Point>()

        fun clearMarkers() {
            markers.clear()
            circleAnnotationManager.deleteAll()
        }

        fun showMarker(coordinate: Point) {
            clearMarkers()

            val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
                .withPoint(coordinate)
                .withCircleRadius(8.0)
                .withCircleColor("#ee4e8b")
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")

            val annotation = circleAnnotationManager.create(circleAnnotationOptions)
            markers[annotation.id] = coordinate

            CameraOptions.Builder()
                .center(coordinate)
                .padding(MARKERS_INSETS_OPEN_CARD)
                .zoom(14.0)
                .build().also {
                    mapboxMap.setCamera(it)
                }
        }
    }

    private fun openPlaceCard(suggestion: PlaceAutocompleteSuggestion) {
        ignoreNextQueryUpdate = true
        queryEditText.setText("")

        lifecycleScope.launchWhenStarted {
            placeAutocomplete.select(suggestion).onValue { result ->
                mapMarkersManager.showMarker(result.coordinate)
                searchPlaceView.open(SearchPlace.createFromPlaceAutocompleteResult(result))

                searchResultsView.isVisible = false
            }.onError { error ->
                Timber.d(LOG_TAG, "Suggestion selection error", error)
                showToast(R.string.place_autocomplete_selection_error)
            }
        }
    }

    private fun closePlaceCard() {
        searchPlaceView.hide()
        mapMarkersManager.clearMarkers()
    }

    private fun geoIntent(point: Point): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${point.latitude()}, ${point.longitude()}"))

    private fun shareIntent(searchPlace: SearchPlace): Intent {
        val text = "${searchPlace.name}. " +
                "Address: ${searchPlace.address?.formattedAddress(SearchAddress.FormatStyle.Short) ?: "unknown"}. " +
                "Geo coordinate: (lat=${searchPlace.coordinate.latitude()}, lon=${searchPlace.coordinate.longitude()})"

        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }

    private fun lacksLocationPermission(): Boolean {
        val activity: Activity = requireActivity()
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(@StringRes resId: Int): Unit {
        Toast.makeText(requireActivity(), resId, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard(v: View) {
        val inputManager = (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun requestLocation() {
        Timber.d("requestContacts()")
        if (lacksLocationPermission()) {
            mActivityResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            showLocation()
        }
    }

    private fun showLocation() {
        val locationService : LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider : DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
        } else {
            Timber.e(LOG_TAG, "Failed to get device location provider")
        }

        locationProvider?.addLocationObserver(locationObserver)

        placeAutocomplete = PlaceAutocomplete.create()
    }

    val locationObserver = LocationObserver { locations ->
        Timber.i(LOG_TAG, "Location update, received: $locations")
        if (locations.size > 0) {
            val location: Location = locations.first()
            val point: Point = Point.fromLngLat(location.longitude, location.latitude)
            val builder = CameraOptions.Builder()
                .center(point)
                .zoom(9.0)
                .build()
            mapView.mapboxMap.setCamera(builder)
        }
    }

    private companion object {

        const val PERMISSIONS_REQUEST_LOCATION = 0

        const val LOG_TAG = "MapsSearchAutocompleteFragment"

        val MARKERS_EDGE_OFFSET = dpToPx(64).toDouble()
        val PLACE_CARD_HEIGHT = dpToPx(300).toDouble()
        val MARKERS_TOP_OFFSET = dpToPx(88).toDouble()

        val MARKERS_INSETS_OPEN_CARD = EdgeInsets(
            MARKERS_TOP_OFFSET, MARKERS_EDGE_OFFSET, PLACE_CARD_HEIGHT, MARKERS_EDGE_OFFSET
        )

        val REGION_LEVEL_TYPES = listOf(
            PlaceAutocompleteType.AdministrativeUnit.Country,
            PlaceAutocompleteType.AdministrativeUnit.Region
        )

        val DISTRICT_LEVEL_TYPES = REGION_LEVEL_TYPES + listOf(
            PlaceAutocompleteType.AdministrativeUnit.Postcode,
            PlaceAutocompleteType.AdministrativeUnit.District
        )

        val LOCALITY_LEVEL_TYPES = DISTRICT_LEVEL_TYPES + listOf(
            PlaceAutocompleteType.AdministrativeUnit.Place,
            PlaceAutocompleteType.AdministrativeUnit.Locality
        )

        private val ALL_TYPES = listOf(
            PlaceAutocompleteType.Poi,
            PlaceAutocompleteType.AdministrativeUnit.Country,
            PlaceAutocompleteType.AdministrativeUnit.Region,
            PlaceAutocompleteType.AdministrativeUnit.Postcode,
            PlaceAutocompleteType.AdministrativeUnit.District,
            PlaceAutocompleteType.AdministrativeUnit.Place,
            PlaceAutocompleteType.AdministrativeUnit.Locality,
            PlaceAutocompleteType.AdministrativeUnit.Neighborhood,
            PlaceAutocompleteType.AdministrativeUnit.Street,
            PlaceAutocompleteType.AdministrativeUnit.Address,
        )

        private fun dpToPx(dp: Int): Int =
            (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}