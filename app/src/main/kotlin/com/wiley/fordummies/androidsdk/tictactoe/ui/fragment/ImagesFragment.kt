package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
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
 * June 2, 2025: Uses PhotoPicker component from Google Play. Sources:
 *   - https://developer.android.com/training/data-storage/shared/photopicker
 *   - https://developer.android.com/about/versions/14/changes/partial-photo-video-access
 *
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class ImagesFragment : Fragment(), View.OnClickListener {
    private lateinit var mImageView: ImageView
    private lateinit var mButtonShow: Button
    private lateinit var mButtonCapture: Button
    private lateinit var mButtonSelect: Button
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
                    requireActivity().resources, R.drawable.image_placeholder
                )
                mBitmapLiveData.postValue(placeholder)
                mImageView.setImageBitmap(placeholder)
                mBitmap = result.scale(dstWidth, dstHeight, false)
                mBitmapLiveData.postValue(mBitmap)
                mImageView.setImageBitmap(mBitmap)
            }
            runnable.run()
        }
    }


    private var mPickPhotoResult =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Timber.tag("PhotoPicker").d("Selected URI: $uri")
                val runnable = Runnable {
                    val placeholder = BitmapFactory.decodeResource(
                        requireActivity().resources, R.drawable.image_placeholder
                    )
                    mBitmapLiveData.postValue(placeholder)
                    mImageView.setImageBitmap(placeholder)
                    mBitmap = uriToBitmap(uri)
                    mBitmapLiveData.postValue(mBitmap)
                    mImageView.setImageBitmap(mBitmap)
                    mImageView.contentDescription = "Image was set"
                }
                runnable.run()
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
                mPickPhotoResult.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            } else {
                // Show error message
                // The user denied permission to view images, so show them a message.
                Timber.e("Error: Permission denied to read image")
                if (lacksImagePermission()) {
                    val activity = requireActivity() as AppCompatActivity
                    val fm = activity.supportFragmentManager
                    val dialogFragment = ReadImagePermissionDeniedDialogFragment()
                    dialogFragment.show(fm, "read_image_perm_denied")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_images, container, false)
        mImageView = v.findViewById(R.id.imageView)
        mButtonShow = v.findViewById<Button>(R.id.buttonImageShow)
        mButtonShow.setOnClickListener(this)
        mButtonCapture = v.findViewById<Button>(R.id.buttonImageCapture)
        mButtonCapture.setOnClickListener(this)
        mButtonSelect = v.findViewById<Button>(R.id.buttonImageSelect)
        mButtonSelect.setOnClickListener(this)
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
            npe.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mButtonShow.setOnClickListener(null)
        mButtonCapture.setOnClickListener(null)
        mButtonSelect.setOnClickListener(null)
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
            if (lacksImagePermission()) {
                // Permission request logic
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    mRequestPermissions.launch(
                        arrayOf(
                            READ_MEDIA_IMAGES,
                            READ_MEDIA_VISUAL_USER_SELECTED
                        )
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mRequestPermissions.launch(arrayOf(READ_MEDIA_IMAGES))
                } else {
                    mRequestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
                }
            } else {
                // Launch the photo picker and let the user choose only images.
                mPickPhotoResult.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
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
                    fileDescriptor, mImageView.drawable.bounds, optionsForBounds
                )

                val srcWidth = optionsForBounds.outWidth.toFloat()
                val srcHeight = optionsForBounds.outHeight.toFloat()
                Timber.tag(TAG).d("srcWidth = %f; srcHeight = %f", srcWidth, srcHeight)

                var inSampleSize = 1

                if (srcWidth > dstWidth || srcHeight > dstHeight) {
                    val widthScale = srcWidth / dstWidth
                    val heightScale = srcHeight / dstHeight

                    val sampleScale = max(widthScale.toDouble(), heightScale.toDouble()).toFloat()
                    inSampleSize = Math.round(sampleScale)
                    Timber.tag(TAG).d("inSampleSize = %d", inSampleSize)
                }

                val actualOptions = BitmapFactory.Options()
                actualOptions.inSampleSize = inSampleSize

                image = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor, mImageView.drawable.bounds, actualOptions
                )
                // largeBitmap.recycle();
                parcelFileDescriptor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun lacksImagePermission(): Boolean {
        var lacksPerms = false
        val context: Context = requireContext()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                context,
                READ_MEDIA_IMAGES
            ) == PERMISSION_GRANTED
        ) {
            // Full access on Android 13 (API level 33) or higher
            Timber.tag(TAG).d("Android 13+; we have full permission to read images")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
                context, READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
            Timber.tag(TAG).d("Android 14+; we have partial permission to read images")
        } else if (ContextCompat.checkSelfPermission(
                context, READ_EXTERNAL_STORAGE
            ) == PERMISSION_GRANTED
        ) {
            // Full access up to Android 12 (API level 32)
            Timber.tag(TAG).d("Android 12 or lower: we have full permission to read images")
        } else {
            lacksPerms = true
            Timber.tag(TAG).i("We LACK permission to read images")
        }

        return lacksPerms
    }
}
