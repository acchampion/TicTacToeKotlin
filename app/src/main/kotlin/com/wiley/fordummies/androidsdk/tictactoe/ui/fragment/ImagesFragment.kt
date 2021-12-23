package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File

/**
 * Fragment for showing and capturing images.
 *
 * Now includes ActivityResult launchers.
 *
 * Created by adamcchampion on 2017/08/12.
 */
class ImagesFragment : Fragment(), View.OnClickListener {
    private lateinit var mImageView: ImageView
    private lateinit var mImageFilePath: String
    private val mBitmapLiveData = MutableLiveData<Bitmap>()

    private var mCapturePhotoLaunch = registerForActivityResult(
        TakePicturePreview()
    ) { result ->
        mBitmapLiveData.value = result
        mImageView.setImageBitmap(mBitmapLiveData.value)
    }

    private var mPickImageResult = registerForActivityResult(
        GetContent()
    ) { result ->
        val uriString = result.toString()
        val imageUri = Uri.parse(uriString)
        mImageView.setImageURI(imageUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_images, container, false)
        mImageView = v.findViewById(R.id.imageView)
        val buttonShow = v.findViewById<Button>(R.id.buttonImageShow)
        buttonShow.setOnClickListener(this)
        val buttonCapture = v.findViewById<Button>(R.id.buttonImageCapture)
        buttonCapture.setOnClickListener(this)
        val buttonSelect = v.findViewById<Button>(R.id.buttonImageSelect)
        buttonSelect.setOnClickListener(this)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mImageFilePath = imageDir!!.path + File.separator + "sample_image.jpg"
    }

    override fun onResume() {
        super.onResume()
        try {
            val activity = requireActivity() as AppCompatActivity
            val actionBar = activity.supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = resources.getString(R.string.images)
            }
        } catch (npe: NullPointerException) {
            Timber.e("Could not set subtitle")
        }
    }

    override fun onClick(view: View) {
        val viewId = view.id
        if (viewId == R.id.buttonImageShow) {
            val imageFile = File(mImageFilePath)
            if (imageFile.exists()) {
                val imageBitmap = BitmapFactory.decodeFile(mImageFilePath)
                mImageView.setImageBitmap(imageBitmap)
            } else {
                // File doesn't exist, so load a sample SVG image.
                // Disable hardware acceleration for SVGs
                mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                mImageView.setImageResource(R.drawable.ic_scoreboard)
            }
        } else if (viewId == R.id.buttonImageCapture) {
            mCapturePhotoLaunch.launch(null)
        } else if (viewId == R.id.buttonImageSelect) {
            mPickImageResult.launch("image/*")
        } else {
            Timber.e("Invalid button click")
        }
    }

}
