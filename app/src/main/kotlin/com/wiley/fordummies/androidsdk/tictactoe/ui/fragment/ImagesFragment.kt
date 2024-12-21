package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber
import java.io.File
import java.io.IOException
import kotlin.math.max


/**
 * Fragment for showing and capturing images.
 *
 * Now includes ActivityResult launchers.
 *
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class ImagesFragment : Fragment(), View.OnClickListener {
    private lateinit var mImageView: ImageView
    private lateinit var mImageFilePath: String
    private val mBitmapLiveData = MutableLiveData<Bitmap?>()
    private var mBitmap: Bitmap? = null

    private val TAG: String = javaClass.simpleName

    private var mCapturePhotoLaunch = registerForActivityResult(
        TakePicturePreview()
    ) { result ->
        val dstWidth = mImageView.width
        val dstHeight = mImageView.height
        if (result != null) {
            val runnable = Runnable {
                val placeholder = BitmapFactory.decodeResource(
                    requireActivity().resources,
                    R.drawable.image_placeholder
                )
                mBitmapLiveData.postValue(placeholder)
                mImageView.setImageBitmap(placeholder)
                mBitmap = Bitmap.createScaledBitmap(result, dstWidth, dstHeight, false)
                mBitmapLiveData.postValue(mBitmap)
                mImageView.setImageBitmap(mBitmap)
            }
            runnable.run()
        }
	}

    private var mPickImageResult = registerForActivityResult(
        GetContent()
    ) { result ->
        if (result != null) {
            val uriString = result.toString()
            val imageUri = Uri.parse(uriString)
            val runnable = Runnable {
                val placeholder = BitmapFactory.decodeResource(
                    requireActivity().resources,
                    R.drawable.image_placeholder
                )
                mBitmapLiveData.postValue(placeholder)
                mImageView.setImageBitmap(placeholder)
                mBitmap = uriToBitmap(imageUri)
                mBitmapLiveData.postValue(mBitmap)
                mImageView.setImageBitmap(mBitmap)
                mImageView.contentDescription = "Image was set"
            }
            runnable.run()
        }
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

    /**
     * Decodes the Bitmap captured by the Camera, and returns the Bitmap. Adapted from Chapter 16
     * in the "Big Nerd Ranch Guide to Android Development", fourth edition.
     *
     * @param selectedFileUri Uri corresponding to the Bitmap to decode
     * @return The scaled Bitmap for the ImageView
     */
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        var image: Bitmap? = null
        var parcelFileDescriptor: ParcelFileDescriptor? = null

        try {
            val activity: Activity = requireActivity()
            parcelFileDescriptor = activity.contentResolver.openFileDescriptor(selectedFileUri, "r")
            if (parcelFileDescriptor != null) {
                val fileDescriptor = parcelFileDescriptor.fileDescriptor

                // Get the bounds
                val optionsForBounds = BitmapFactory.Options()
                optionsForBounds.inJustDecodeBounds = true

                val dstWidth = mImageView.width
                val dstHeight = mImageView.height
                Timber.tag(TAG).d("dstWidth = %d; dstHeight = %d", dstWidth, dstHeight)

                BitmapFactory.decodeFileDescriptor(
                    fileDescriptor,
                    mImageView.drawable.bounds,
                    optionsForBounds
                )

                val srcWidth = optionsForBounds.outWidth.toFloat()
                val srcHeight = optionsForBounds.outHeight.toFloat()
                Timber.tag(TAG).d("srcWidth = %f; srcHeight = %f", srcWidth, srcHeight)

                var inSampleSize = 1

                if (srcWidth > dstWidth || srcHeight > dstHeight) {
                    val widthScale = srcWidth / dstWidth
                    val heightScale = srcHeight / dstHeight

                    val sampleScale =
                        max(widthScale.toDouble(), heightScale.toDouble()).toFloat()
                    inSampleSize = Math.round(sampleScale)
                    Timber.tag(TAG).d("inSampleSize = %d", inSampleSize)
                }

                val actualOptions = BitmapFactory.Options()
                actualOptions.inSampleSize = inSampleSize

                image = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor,
                    mImageView.drawable.bounds,
                    actualOptions
                )
                // largeBitmap.recycle();
                parcelFileDescriptor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

}
