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
import androidx.annotation.Keep
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber

/**
 * Created by acc on July 10, 2023.
 *
 * I modified this example based on <a href="https://docs.mapbox.com/android/maps/examples/location-tracking/">a
 *   Mapbox example app/a>.
 */
@Keep
class MapsLocationFragment : Fragment(), PermissionsListener, MenuProvider {
	private val permissionsManager = PermissionsManager(this)

	private lateinit var mapView: MapView

	private val TAG = javaClass.simpleName

	private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
		mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
	}

	private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
		val mapboxMap: MapboxMap = mapView.mapboxMap
		mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
		mapView.gestures.focalPoint = mapboxMap.pixelForCoordinate(it)
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
	): View {
		super.onCreateView(inflater, container, savedInstanceState)

		val v: View = inflater.inflate(R.layout.fragment_maps_location, container, false)
		mapView = v.findViewById<View>(R.id.map_view_location) as MapView
		setupMap(mapView)

		return v
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val menuHost: MenuHost = requireActivity()
		menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		with (mapView) {
			location.enabled = false
			location.puckBearingEnabled = false
			location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
			location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
			gestures.removeOnMoveListener(onMoveListener)
		}
		val menuHost: MenuHost = requireActivity()
		menuHost.removeMenuProvider(this)
	}


	private fun setupMap(mapView: MapView) {
		val cameraOptions = CameraOptions.Builder()
			.zoom(14.0)
			.build()
		val mapboxMap = mapView.mapboxMap
		mapboxMap.setCamera(cameraOptions)
		mapboxMap.loadStyle(Style.STANDARD)

		with (mapView) {
			location.locationPuck = createDefault2DPuck(withBearing = true)
			location.enabled = true
			location.puckBearing = PuckBearing.COURSE
			location.puckBearingEnabled = true
			viewport.transitionTo(
				targetState = viewport.makeFollowPuckViewportState(),
				transition = viewport.makeImmediateViewportTransition()
			)
			location.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
			location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
			gestures.addOnMoveListener(onMoveListener)
		}
	}

	private fun onCameraTrackingDismissed() {
		Timber.tag(TAG).d("onCameraTrackingDismissed()")
		Toast.makeText(requireActivity(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
		with (mapView) {
			location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
			location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
			gestures.removeOnMoveListener(onMoveListener)
		}
	}

	override fun onExplanationNeeded(permissionsToExplain: List<String>) {
		val ctx = requireContext()
		Timber.tag(TAG).d("onExplanationNeeded()")
		Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show()
	}

	override fun onPermissionResult(granted: Boolean) {
		val ctx = requireContext()
		if (granted) {
			setupMap(mapView)
			Timber.tag(TAG).e( "User granted location permission")
		} else {
			Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show()
			Timber.tag(TAG).d( "User denied location permission")
		}
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_maps, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		val itemId = menuItem.itemId
		val activity: Activity = requireActivity()
		if (itemId == R.id.menu_showcurrentlocation) {
			if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
				permissionsManager.requestLocationPermissions(activity)
			} else {
				setupMap(mapView)
			}
		}
		return false
	}
}
