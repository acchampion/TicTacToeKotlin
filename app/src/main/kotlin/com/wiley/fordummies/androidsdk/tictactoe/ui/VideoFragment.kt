package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * Created by adamcchampion on 2017/08/12.
 */
class VideoFragment : Fragment(), View.OnClickListener {
    private lateinit var mVideoView: VideoView
    private var mVideoFileUri: Uri? = null
    private lateinit var mButtonStart: Button
    private lateinit var mButtonStop: Button
    private val mRecordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

    private var mCaptureVideoResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                mVideoFileUri = intent.data
                mVideoView.setVideoURI(mVideoFileUri)
                Timber.v("Audio File URI: %s", mVideoFileUri)
            }
        }
    }
    private var mPickVideoResult = registerForActivityResult(
        GetContent()
    ) { result ->
        val uriString = result.toString()
        mVideoFileUri = Uri.parse(uriString)
        mVideoView.setVideoURI(mVideoFileUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_video, container, false)
        mVideoView = v.findViewById(R.id.videoView)
        mButtonStart = v.findViewById(R.id.buttonVideoStart)
        mButtonStart.setOnClickListener(this)
        mButtonStop = v.findViewById(R.id.buttonVideoStop)
        mButtonStop.setOnClickListener(this)
        val buttonRecord = v.findViewById<Button>(R.id.buttonVideoRecord)
        buttonRecord.setOnClickListener(this)
        val buttonSelect = v.findViewById<Button>(R.id.buttonVideoSelect)
        buttonSelect.setOnClickListener(this)

//		// Guard against no video recorder app (disable the "record" button).
//		PackageManager packageManager = activity.getPackageManager();
//		if (packageManager.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
//			buttonRecord.setEnabled(false);
//		}
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = requireContext()
        val videoDir = ctx.applicationContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val videoFilePath = videoDir!!.path + File.separator + "sample_video.mp4"
        val videoFile = File(videoFilePath)
        mVideoFileUri = if (Objects.requireNonNull(videoFile).exists()) {
            Uri.fromFile(videoFile)
        } else {
            // Video file doesn't exist, so load sample video from resources.
            val videoResourceName = "android.resource://" + ctx.packageName +
                    File.separator + R.raw.sample_video
            Uri.parse(videoResourceName)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val activity = requireActivity() as AppCompatActivity
            val actionBar = activity.supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = resources.getString(R.string.video)
            }
        } catch (npe: NullPointerException) {
            Timber.e("Could not set subtitle")
        }
    }

    override fun onClick(view: View) {
		when (view.id) {
			R.id.buttonVideoStart -> {
				// Load and start the movie
				mVideoView.setVideoURI(mVideoFileUri)
				mVideoView.start()
				// Make the start button inactive and the stop button active
				mButtonStart.isEnabled = false
				mButtonStop.isEnabled = true
			}
			R.id.buttonVideoStop -> {
				mVideoView.stopPlayback()
				// Make the start button active and the stop button inactive
				mButtonStart.isEnabled = true
				mButtonStop.isEnabled = false
			}
			R.id.buttonVideoRecord -> {
				mCaptureVideoResult.launch(mRecordVideoIntent)
			}
			R.id.buttonVideoSelect -> {
				mPickVideoResult.launch("video/*")
			}
			else -> {
				Timber.e("Invalid button press")
			}
		}
    }
}
