package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.MapsSearchFragment

class MapsSearchActivity : SingleFragmentActivity() {

	override fun createFragment(): Fragment {
		return MapsSearchFragment()
	}
}
