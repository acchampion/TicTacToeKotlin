package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.MapsLocationFragment

class MapsLocationActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return MapsLocationFragment()
	}
}
