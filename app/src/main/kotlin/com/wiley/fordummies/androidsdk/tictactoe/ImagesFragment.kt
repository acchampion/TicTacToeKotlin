package com.wiley.fordummies.androidsdk.tictactoe

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

import java.io.File

import android.app.Activity.RESULT_OK
import android.support.v7.app.AppCompatActivity

/**
 * Fragment for showing and capturing images.
 *
 * Created by adamcchampion on 2017/08/12.
 */

class ImagesFragment : Fragment(), View.OnClickListener {
    private lateinit var imageView: ImageView
    private val imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path +
            File.separator + "other_image.png"
    private lateinit var imageBitmap: Bitmap

    private val mCaptureImageIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
    private val IMAGE_CAPTURED = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_images, container, false)

        imageView = v.findViewById(R.id.imageView) as ImageView

        val buttonShow: Button = v.findViewById(R.id.buttonImageShow) as Button
        buttonShow.setOnClickListener(this)
        val buttonCapture: Button = v.findViewById(R.id.buttonImageCapture) as Button
        buttonCapture.setOnClickListener(this)
        val buttonExit: Button = v.findViewById(R.id.buttonImageExit) as Button
        buttonExit.setOnClickListener(this)

        return v
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.images)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonImageShow -> {
                val imageFile = File(imageFilePath)
                if (imageFile.exists()) {
                    imageBitmap = BitmapFactory.decodeFile(imageFilePath)
                    imageView.setImageBitmap(imageBitmap)
                } else {
                    // File doesn't exist, so load a sample SVG image.
                    // Disable hardware acceleration for SVGs
                    imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    imageView.setImageResource(R.drawable.ic_scoreboard)
                }
            }
            R.id.buttonImageCapture -> startActivityForResult(mCaptureImageIntent, IMAGE_CAPTURED)
            R.id.buttonImageExit -> activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, cameraIntent: Intent?) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURED) {
            val extras = cameraIntent?.extras
            if (extras != null) {
                imageBitmap = extras.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
            }
        }
    }
}
