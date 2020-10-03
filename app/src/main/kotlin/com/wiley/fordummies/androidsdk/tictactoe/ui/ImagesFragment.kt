package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import java.io.File

/**
 * Fragment for showing and capturing images.
 *
 * Created by adamcchampion on 2017/08/12.
 */

class ImagesFragment : Fragment(), View.OnClickListener {
    private lateinit var mImageView: ImageView
    private lateinit var mImageFilePath: String
    private lateinit var mImageBitmap: Bitmap

    private val mCaptureImageIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
    private val IMAGE_CAPTURED = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_images, container, false)

        mImageView = v.findViewById(R.id.imageView)

        val buttonShow: Button = v.findViewById(R.id.buttonImageShow)
        buttonShow.setOnClickListener(this)
        val buttonCapture: Button = v.findViewById(R.id.buttonImageCapture)
        buttonCapture.setOnClickListener(this)

        // Guard against no camera app (disable the "record" button).
		val activity = requireActivity()
        val packageManager = activity.packageManager
        if (packageManager.resolveActivity(mCaptureImageIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            buttonCapture.isEnabled = false
        }

        return v
    }

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val musicPath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		mImageFilePath = musicPath!!.path + File.separator + "other_image.png"
	}

    override fun onResume() {
        super.onResume()
		val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            subtitle = resources.getString(R.string.images)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonImageShow -> {
                val imageFile = File(mImageFilePath)
                if (imageFile.exists()) {
                    mImageBitmap = BitmapFactory.decodeFile(mImageFilePath)
                    mImageView.setImageBitmap(mImageBitmap)
                } else {
                    // File doesn't exist, so load a sample SVG image.
                    // Disable hardware acceleration for SVGs
                    mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    mImageView.setImageResource(R.drawable.ic_scoreboard)
                }
            }
            R.id.buttonImageCapture -> startActivityForResult(mCaptureImageIntent, IMAGE_CAPTURED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, cameraIntent: Intent?) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURED) {
            val extras = cameraIntent?.extras
            if (extras != null) {
                mImageBitmap = extras.get("data") as Bitmap
                mImageView.setImageBitmap(mImageBitmap)
            }
        }
    }
}
