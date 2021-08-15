package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File

/**
 * Audio playback Fragment.
 *
 * Now includes ActivityResultLaunchers.
 *
 * Created by adamcchampion on 2017/08/12.
 */
class AudioFragment : Fragment(), View.OnClickListener {
    private var mStarted = false
    private var mAudioFileUri: Uri? = null
    private val mRecordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
    private lateinit var mButtonStart: Button
    private lateinit var mButtonStop: Button

    private var mRecordAudioResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                mAudioFileUri = intent.data
                Timber.v("Audio File URI: %s", mAudioFileUri)
            }
        }
    }
    private var mPickAudioResult = registerForActivityResult(
        GetContent()
    ) { result ->
        val uriString = result.toString()
        mAudioFileUri = Uri.parse(uriString)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audio, container, false)
        mButtonStart = v.findViewById(R.id.buttonAudioStart)
        mButtonStart.setOnClickListener(this)
        mButtonStop = v.findViewById(R.id.buttonAudioStop)
        mButtonStop.setOnClickListener(this)
        val buttonRecord = v.findViewById<Button>(R.id.buttonAudioRecord)
        buttonRecord.setOnClickListener(this)
        val buttonSelect = v.findViewById<Button>(R.id.buttonAudioSelect)
        buttonSelect.setOnClickListener(this)

        // Guard against no audio recorder app (disable the "record" button).
        val activity: Activity = requireActivity()
        val packageManager = activity.packageManager
        if (packageManager.resolveActivity(
                mRecordAudioIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            buttonRecord.isEnabled = false
        }
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = requireContext()
        val musicDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val mAudioFilePath = musicDir!!.path + File.separator + "sample_audio.mp3"
        val audioFile = File(mAudioFilePath)
        mAudioFileUri = if (audioFile.exists()) {
            Uri.fromFile(audioFile)
        } else {
            // Audio file doesn't exist, so load sample audio from resources.
            val audioResourceName = "android.resource://" + ctx.packageName +
                    File.separator + R.raw.sample_audio
            Uri.parse(audioResourceName)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val activity = requireActivity() as AppCompatActivity
            val actionBar = activity.supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = resources.getString(R.string.audio)
            }
        } catch (npe: NullPointerException) {
            Timber.e("Could not set subtitle")
        }
    }

    override fun onClick(view: View) {
        val activity: Activity = requireActivity()
        val viewId = view.id
        if (viewId == R.id.buttonAudioStart) {
            if (!mStarted) {
                val musicIntent =
                    Intent(activity.applicationContext, MediaPlaybackService::class.java)
                musicIntent.putExtra("URIString", mAudioFileUri.toString())
                Timber.d("URI: %s", mAudioFileUri.toString())
                activity.startService(musicIntent)
                mStarted = true
                mButtonStart.isEnabled = false
                mButtonStop.isEnabled = true
            }
        } else if (viewId == R.id.buttonAudioStop) {
            activity.stopService(
                Intent(
                    activity.applicationContext,
                    MediaPlaybackService::class.java
                )
            )
            mStarted = false
            mButtonStart.isEnabled = true
            mButtonStop.isEnabled = false
        } else if (viewId == R.id.buttonAudioRecord) {
            mRecordAudioResult.launch(Intent(mRecordAudioIntent))
        } else if (viewId == R.id.buttonAudioSelect) {
            mPickAudioResult.launch("audio/*")
        } else {
            Timber.e("Invalid button click")
        }
    }
}
