package com.wiley.fordummies.androidsdk.tictactoe

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView

import java.io.File

import android.app.Activity.RESULT_OK

/**
 * Created by adamcchampion on 2017/08/12.
 */

class VideoFragment : Fragment(), View.OnClickListener {
    private lateinit var mButtonStart: Button
    private lateinit var mButtonStop: Button
    private lateinit var mButtonRecord: Button
    private lateinit var mVideoView: VideoView
    private var mVideoFileUri: Uri? = null
    private val mRecordVideoIntent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)

    private val VIDEO_CAPTURED = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_video, container, false)

        mVideoView = v.findViewById(R.id.videoView)

        mButtonStart = v.findViewById(R.id.buttonVideoStart)
        mButtonStart.setOnClickListener(this)
        mButtonStop = v.findViewById(R.id.buttonVideoStop)
        mButtonStop.setOnClickListener(this)
        mButtonRecord = v.findViewById(R.id.buttonVideoRecord)
        mButtonRecord.setOnClickListener(this)

        val btnExit: Button = v.findViewById(R.id.buttonVideoExit)
        btnExit.setOnClickListener(this)

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path +
                File.separator + "sample_video.mp4"
        val videoFile = File(path)
        if (videoFile.exists()) {
            mVideoFileUri = Uri.fromFile(videoFile)
        } else {
            // Video file doesn't exist, so load sample video from resources.
            val videoResourceName = "android.resource://" + activity?.packageName +
                    File.separator + R.raw.sample_video
            mVideoFileUri = Uri.parse(videoResourceName)
        }

        // Guard against no audio recorder app (disable the "record" button).
        val packageManager = activity?.packageManager
        if (packageManager?.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mButtonRecord.isEnabled = false
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.video)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonVideoStart -> {
                // Load and start the movie
                mVideoView.setVideoURI(mVideoFileUri)
                mVideoView.start()
            }
            R.id.buttonVideoRecord -> startActivityForResult(mRecordVideoIntent, VIDEO_CAPTURED)
            R.id.buttonVideoStop -> mVideoView.stopPlayback()
            R.id.buttonVideoExit -> activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            if (data != null) {
                mVideoFileUri = data.data
            }
        }
    }
}
