package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.AudioActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.ContactsActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.GameSessionActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.ImagesActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.MapsLocationActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.PhotoGalleryActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.SensorsActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.SettingsActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.VideoActivity
import timber.log.Timber


/**
 * Fragment that handles main user navigation in the app.
 *
 * Created by adamcchampion on 2017/08/05.
 */
@Keep
class GameOptionsFragment : Fragment(), View.OnClickListener, MenuProvider {

	private val classTag = javaClass.simpleName

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Timber.tag(classTag).d("onCreateView")
		val v = inflater.inflate(R.layout.fragment_game_options, container, false)

		val btnNewGame: Button = v.findViewById(R.id.buttonNewGame)
		btnNewGame.setOnClickListener(this)
		val btnAudio: Button = v.findViewById(R.id.buttonAudio)
		btnAudio.setOnClickListener(this)
		val btnVideo: Button = v.findViewById(R.id.buttonVideo)
		btnVideo.setOnClickListener(this)
		val btnImage: Button = v.findViewById(R.id.buttonImages)
		btnImage.setOnClickListener(this)
		val btnMapsLocation = v.findViewById<Button>(R.id.buttonMapsLocation)
		btnMapsLocation.setOnClickListener(this)
		val btnMapsSearch = v.findViewById<Button>(R.id.buttonMapsSearch)
		btnMapsSearch.isEnabled = false
		//btnMapsSearch.setOnClickListener(this)
		val btnSettings: Button = v.findViewById(R.id.buttonSettings)
		btnSettings.setOnClickListener(this)
		val btnHelp: Button = v.findViewById(R.id.buttonHelp)
		btnHelp.setOnClickListener(this)
		val btnTestSensors: Button = v.findViewById(R.id.buttonSensors)
		btnTestSensors.setOnClickListener(this)
		val btnPhotoGallery: Button = v.findViewById(R.id.buttonPhotoGallery)
		btnPhotoGallery.setOnClickListener(this)
		val btnExit: Button = v.findViewById(R.id.buttonExit)
		btnExit.setOnClickListener(this)

		return v
	}

	override fun onResume() {
		super.onResume()
		val activity = requireActivity() as AppCompatActivity
		activity.supportActionBar?.apply {
			subtitle = resources.getString(R.string.options)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val menuHost: MenuHost = requireActivity()
		menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}


	override fun onDestroyView() {
		super.onDestroyView()
		val activity: Activity = requireActivity()
		val appContext: Context = activity.applicationContext
		activity.stopService(Intent(appContext, MediaPlaybackService::class.java))

		val menuHost: MenuHost = requireActivity()
		menuHost.removeMenuProvider(this)
	}

	override fun onClick(v: View) {
		val activity = requireActivity()
		when (v.id) {
			R.id.buttonNewGame -> startActivity(
				Intent(
					activity.applicationContext,
					GameSessionActivity::class.java
				)
			)
			R.id.buttonAudio -> startActivity(
				Intent(
					activity.applicationContext,
					AudioActivity::class.java
				)
			)
			R.id.buttonVideo -> startActivity(
				Intent(
					activity.applicationContext,
					VideoActivity::class.java
				)
			)
			R.id.buttonImages -> startActivity(
				Intent(
					activity.applicationContext,
					ImagesActivity::class.java
				)
			)
			R.id.buttonMapsLocation -> startActivity(
				Intent(
					activity.applicationContext,
					MapsLocationActivity::class.java
				)
			)
			/* R.id.buttonMapsSearch -> startActivity(
				Intent(
					activity.applicationContext,
					MapsSearchActivity::class.java
				)
			) */
			R.id.buttonSettings -> startActivity(
				Intent(
					activity.applicationContext,
					SettingsActivity::class.java
				)
			)
			R.id.buttonHelp -> startActivity(
				Intent(
					activity.applicationContext,
					HelpActivity::class.java
				)
			)
			R.id.buttonSensors -> startActivity(
				Intent(
					activity.applicationContext,
					SensorsActivity::class.java
				)
			)
			R.id.buttonPhotoGallery -> {
				startActivity(
					Intent(activity.applicationContext,
						PhotoGalleryActivity::class.java
					)
				)
			}
			R.id.buttonExit -> {
				activity.stopService(
					Intent(
						activity.applicationContext,
						MediaPlaybackService::class.java
					)
				)
				showQuitAppDialog()
			}
		}
	}

	private fun showQuitAppDialog() {
		val manager = parentFragmentManager
		val fragment = QuitAppDialogFragment()
		fragment.show(manager, "quit_app")
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		val activity = requireActivity()
		when (menuItem.itemId) {
			R.id.menu_settings -> {
				startActivity(Intent(activity.applicationContext, SettingsActivity::class.java))
				return true
			}
			R.id.menu_help -> {
				startActivity(Intent(activity.applicationContext, HelpActivity::class.java))
				return true
			}
			R.id.menu_exit -> {
				showQuitAppDialog()
				return true
			}
			R.id.menu_contacts -> {
				startActivity(Intent(activity.applicationContext, ContactsActivity::class.java))
				return true
			}
		}
		return false
	}
}
