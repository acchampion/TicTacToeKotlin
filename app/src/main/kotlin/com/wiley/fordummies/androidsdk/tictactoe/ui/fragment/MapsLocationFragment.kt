package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style.Companion.MAPBOX_STREETS
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.Plugin.Companion.MAPBOX_GESTURES_PLUGIN_ID
import com.mapbox.maps.plugin.Plugin.Companion.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber

/**
 * Created by acc on July 10, 2023.
 *
 * I modified this example based on <a href="https://docs.mapbox.com/android/maps/examples/location-tracking/">a
 *   Mapbox example app/a>.
 */class MapsLocationFragment : Fragment(), PermissionsListener {
	private val permissionsManager = PermissionsManager(this)

	private lateinit var mapView: MapView
	private lateinit var locationPuck: LocationPuck2D
	private lateinit var locationPlugin: LocationComponentPlugin
	private lateinit var gesturesPlugin: GesturesPlugin

	private val TAG = javaClass.simpleName

	private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
		mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
	}

	private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
		val map: MapboxMap = mapView.getMapboxMap()
		mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
		gesturesPlugin.focalPoint = map.pixelForCoordinate(it)
	}

	private val onMoveListener = object : OnMoveListener {
		override fun onMove(detector: MoveGestureDetector): Boolean {
			return false
		}

		override fun onMoveBegin(detector: MoveGestureDetector) {
			onCameraTrackingDismissed()
		}

		override fun onMoveEnd(detector: MoveGestureDetector) {}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		setHasOptionsMenu(true)

		val v: View = inflater.inflate(R.layout.fragment_maps_location, container, false)

		mapView = v.findViewById<View>(R.id.map_view_location) as MapView
		val mMapboxMap: MapboxMap = mapView.getMapboxMap()
		setupMap(mMapboxMap)

		return v
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_maps, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val itemId = item.itemId
		val activity: Activity = requireActivity()
		if (itemId == R.id.menu_showcurrentlocation) {
			if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
				permissionsManager.requestLocationPermissions(activity)
			} else {
				val map: MapboxMap = mapView.getMapboxMap()
				setupMap(map)
			}
		}
		return false
	}

	override fun onDestroyView() {
		super.onDestroyView()
		gesturesPlugin.removeOnMoveListener(onMoveListener)
		locationPlugin.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
		locationPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
	}


	private fun setupMap(map: MapboxMap) {
		val cameraOptions = CameraOptions.Builder()
			.zoom(14.0)
			.build()
		map.setCamera(cameraOptions)
		map.loadStyleUri(MAPBOX_STREETS) {
			initLocation()
			setupGesturesListener()
		}
	}

	private fun setupGesturesListener() {
		gesturesPlugin = mapView.getPlugin(MAPBOX_GESTURES_PLUGIN_ID)!!
		gesturesPlugin.addOnMoveListener(onMoveListener)
	}


	private fun initLocation() {
		val activity: Activity = requireActivity()
		locationPuck = LocationPuck2D(
			bearingImage = AppCompatResources.getDrawable(
				activity,
				com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_puck_icon
			),
			shadowImage = AppCompatResources.getDrawable(
				activity,
				com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon_shadow
			)
		)
		locationPlugin = mapView.getPlugin(MAPBOX_LOCATION_COMPONENT_PLUGIN_ID)!!
		locationPlugin.updateSettings {
			this.enabled = true
			this.locationPuck = LocationPuck2D(
				bearingImage = AppCompatResources.getDrawable(
					activity,
					com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_puck_icon,
				),
				shadowImage = AppCompatResources.getDrawable(
					activity,
					com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon_shadow,
				),
				scaleExpression = interpolate {
					linear()
					zoom()
					stop {
						literal(0.0)
						literal(0.6)
					}
					stop {
						literal(20.0)
						literal(1.0)
					}
				}.toJson()
			)
		}
		locationPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
		locationPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
	}


	private fun onCameraTrackingDismissed() {
		Timber.tag(TAG).d("onCameraTrackingDismissed()")
		Toast.makeText(requireActivity(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
		locationPlugin.removeOnIndicatorPositionChangedListener(
			onIndicatorPositionChangedListener
		)
		locationPlugin.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
		gesturesPlugin.removeOnMoveListener(onMoveListener)
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
			setupGesturesListener()
			Timber.tag(TAG).e( "User granted location permission")
		} else {
			Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show()
			Timber.tag(TAG).d( "User denied location permission")
		}
	}
}
