package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File

/**
 * Audio playback Fragment.
 *
 * Created by adamcchampion on 2017/08/12.
 */
class AudioFragment : Fragment(), View.OnClickListener {
    private var mStarted = false
    private lateinit var mAudioFilePath: String
    private lateinit var mAudioFileUri: Uri
    private val mRecordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)

    private val AUDIO_CAPTURED = 1

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_audio, container, false)

        val buttonStart: Button = v.findViewById(R.id.buttonAudioStart)
        buttonStart.setOnClickListener(this)
        val buttonStop: Button = v.findViewById(R.id.buttonAudioStop)
        buttonStop.setOnClickListener(this)
        val buttonRecord: Button = v.findViewById(R.id.buttonAudioRecord)
        buttonRecord.setOnClickListener(this)

        // Guard against no audio recorder app (disable the "record" button).
        val packageManager = requireActivity().packageManager
        if (packageManager.resolveActivity(mRecordAudioIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            buttonRecord.isEnabled = false
        }

        return v
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val musicDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
		mAudioFilePath = musicDir!!.path + File.separator + "sample_audio.mp3"
		val audioFile = File(mAudioFilePath)

		mAudioFileUri = if (audioFile.exists()) {
			Uri.fromFile(File(mAudioFilePath))
		} else {
			// Audio file doesn't exist, so load sample audio from resources.
			val audioResourceName = "android.resource://" + requireActivity().packageName +
					File.separator + R.raw.sample_audio
			Uri.parse(audioResourceName)
		}
	}

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.audio)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonAudioStart -> if (!mStarted) {
                val musicIntent = Intent(activity?.applicationContext, MediaPlaybackService::class.java)
                musicIntent.putExtra("URIString", mAudioFileUri.toString())
                Timber.d("URI: $mAudioFileUri")
                activity?.startService(musicIntent)
                mStarted = true
            }
            R.id.buttonAudioStop -> {
                activity?.stopService(Intent(activity?.applicationContext, MediaPlaybackService::class.java))
                mStarted = false
            }
            R.id.buttonAudioRecord -> startActivityForResult(mRecordAudioIntent, AUDIO_CAPTURED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == AUDIO_CAPTURED) {
            if (data != null) {
                mAudioFileUri = data.data!!
                Timber.v("Audio File URI: $mAudioFileUri")
            }
        }
    }
}
