package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.*

/**
 * Fragment that handles main user navigation in the app.
 *
 * Created by adamcchampion on 2017/08/05.
 */

class GameOptionsFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_game_options, container, false)

        val btnNewGame: Button = v.findViewById(R.id.buttonNewGame)
        btnNewGame.setOnClickListener(this)
        val btnAudio: Button = v.findViewById(R.id.buttonAudio)
        btnAudio.setOnClickListener(this)
        val btnVideo: Button = v.findViewById(R.id.buttonVideo)
        btnVideo.setOnClickListener(this)
        val btnImage: Button = v.findViewById(R.id.buttonImages)
        btnImage.setOnClickListener(this)
        val btnMaps: Button = v.findViewById(R.id.buttonMaps)
        btnMaps.setOnClickListener(this)
        val btnSettings: Button = v.findViewById(R.id.buttonSettings)
        btnSettings.setOnClickListener(this)
        val btnHelp: Button = v.findViewById(R.id.buttonHelp)
        btnHelp.setOnClickListener(this)
        val btnTestSensors: Button = v.findViewById(R.id.buttonSensors)
        btnTestSensors.setOnClickListener(this)
        val btnExit: Button = v.findViewById(R.id.buttonExit)
        btnExit.setOnClickListener(this)

        setHasOptionsMenu(true)

        return v
    }

    override fun onResume() {
        super.onResume()
		val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            subtitle = resources.getString(R.string.options)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val activity = requireActivity()
        when (item.itemId) {
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


    override fun onClick(v: View) {
		val activity = requireActivity()
        when (v.id) {
            R.id.buttonNewGame -> startActivity(Intent(activity.applicationContext, GameSessionActivity::class.java))
            R.id.buttonAudio -> startActivity(Intent(activity.applicationContext, AudioActivity::class.java))
            R.id.buttonVideo -> startActivity(Intent(activity.applicationContext, VideoActivity::class.java))
            R.id.buttonImages -> startActivity(Intent(activity.applicationContext, ImagesActivity::class.java))
            R.id.buttonMaps -> startActivity(Intent(activity.applicationContext, MapsActivity::class.java))
            R.id.buttonSettings -> startActivity(Intent(activity.applicationContext, SettingsActivity::class.java))
            R.id.buttonHelp -> startActivity(Intent(activity.applicationContext, HelpActivity::class.java))
            R.id.buttonSensors -> startActivity(Intent(activity.applicationContext, SensorsActivity::class.java))
            R.id.buttonExit -> {
                activity.stopService(Intent(activity.applicationContext, MediaPlaybackService::class.java))
                showQuitAppDialog()
            }
        }
    }

    private fun showQuitAppDialog() {
        val manager = parentFragmentManager
        val fragment = QuitAppDialogFragment()
		fragment.show(manager, "quit_app")
    }

}
