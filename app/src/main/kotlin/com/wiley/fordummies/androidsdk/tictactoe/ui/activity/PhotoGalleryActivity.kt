package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.PhotoGalleryFragment

class PhotoGalleryActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return PhotoGalleryFragment()
    }

    companion object {
		fun newIntent(context: Context?): Intent {
            return Intent(context, PhotoGalleryActivity::class.java)
        }
    }
}
