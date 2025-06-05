package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File
import java.util.Objects

/**
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class VideoFragment : Fragment(), View.OnClickListener {
    private lateinit var mVideoView: VideoView
    private lateinit var mVideoFileUri: Uri
    private lateinit var mButtonStart: Button
    private lateinit var mButtonStop: Button
    private lateinit var mButtonRecord: Button
    private lateinit var mButtonSelect: Button
    private val mRecordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

    private val TAG: String = javaClass.simpleName

    private var mCaptureVideoResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                mVideoFileUri = if (intent.data != null) {
                    intent.data!!
                } else {
                    // Video file doesn't exist, so load sample video from resources.
                    val videoResourceName = "android.resource://" + requireContext().packageName +
                            File.separator + R.raw.sample_video
                    videoResourceName.toUri()
                }
                mVideoView.setVideoURI(mVideoFileUri)
                Timber.v("Audio File URI: %s", mVideoFileUri)
            }
        }
    }

    private var mPickVideoResult =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Timber.tag("PhotoPicker").d("Selected URI: $uri")
                mVideoFileUri = uri
                mVideoView.setVideoURI(uri)
            } else {
                Timber.tag("PhotoPicker").d("No media selected")
            }
        }

    private var mRequestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionResults ->
        {
            if (permissionResults.values.contains(true)) {
                // Launch the photo picker and let the user choose only images.
                mPickVideoResult.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
            } else {
                // Show error message
                // The user denied permission to view videos, so show them a message.
                Timber.e("Error: Permission denied to read videos")
                if (lacksVideoPermission()) {
                    val activity = requireActivity() as AppCompatActivity
                    val fm = activity.supportFragmentManager
                    val dialogFragment = ReadVideoPermissionDeniedDialogFragment()
                    dialogFragment.show(fm, "read_video_perm_denied")
                }
            }
        }
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
        mButtonRecord = v.findViewById<Button>(R.id.buttonVideoRecord)
        mButtonRecord.setOnClickListener(this)
        mButtonSelect = v.findViewById<Button>(R.id.buttonVideoSelect)
        mButtonSelect.setOnClickListener(this)

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
            videoResourceName.toUri()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mButtonStart.setOnClickListener(null)
        mButtonStop.setOnClickListener(null)
        mButtonRecord.setOnClickListener(null)
        mButtonSelect.setOnClickListener(null)
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
                if (lacksVideoPermission()) {
                    // Permission request logic
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        mRequestPermissions.launch(
                            arrayOf(
                                READ_MEDIA_VIDEO,
                                READ_MEDIA_VISUAL_USER_SELECTED
                            )
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mRequestPermissions.launch(arrayOf(READ_MEDIA_VIDEO))
                    } else {
                        mRequestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
                    }
                } else {
                    // Launch the photo picker and let the user choose only video.
                    mPickVideoResult.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
                }
            }

            else -> {
                Timber.e("Invalid button press")
            }
        }
    }

    fun lacksVideoPermission(): Boolean {
        var lacksPerms = false
        val context: Context = requireContext()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                context,
                READ_MEDIA_VIDEO
            ) == PERMISSION_GRANTED
        ) {
            // Full access on Android 13 (API level 33) or higher
            Timber.tag(TAG).d("Android 13+; we have full permission to read video")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
                context, READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
            Timber.tag(TAG).d("Android 14+; we have partial permission to read video")
        } else if (ContextCompat.checkSelfPermission(
                context, READ_EXTERNAL_STORAGE
            ) == PERMISSION_GRANTED
        ) {
            // Full access up to Android 12 (API level 32)
            Timber.tag(TAG).d("Android 12 or lower: we have full permission to read video")
        } else {
            lacksPerms = true
            Timber.tag(TAG).i("We LACK permission to read video")
        }

        return lacksPerms
    }
}
