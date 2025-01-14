package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.MapsSearchAutocompleteFragment

/**
 * Activity that lets the user search for a location on a map.
 *
 * Created by acc in July 2023.
 */
@Keep
class MapsSearchActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return MapsSearchAutocompleteFragment()
	}
}
