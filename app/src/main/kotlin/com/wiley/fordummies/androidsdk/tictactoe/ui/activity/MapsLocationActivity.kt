package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.MapsLocationFragment

/**
 * Activity that shows the user's location on a Mapbox map.
 *
 * Created by acc in July 2023.
 */
@Keep
class MapsLocationActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return MapsLocationFragment()
	}
}
