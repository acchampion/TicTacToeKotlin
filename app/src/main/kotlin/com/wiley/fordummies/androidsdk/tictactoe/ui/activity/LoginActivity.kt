package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.LoginFragment

/**
 * Activity for user login.
 *
 * Created by adamcchampion on 2017/08/03.
 */

class LoginActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return LoginFragment()
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }

	override fun onCreate(savedInstanceState: Bundle?) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			installSplashScreen().setOnExitAnimationListener { splashScreenView: SplashScreenViewProvider ->
				val slideUp = ObjectAnimator.ofFloat(
					splashScreenView, View.TRANSLATION_Y.toString(),
					0f,
					splashScreenView.iconView.height
						.toFloat()
				)
				slideUp.interpolator = AnticipateInterpolator()
				slideUp.duration = 200L

				// Call SplashScreenView.remove at the end of your custom animation.
				slideUp.addListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						splashScreenView.remove()
					}
				})

				// Run your animation.
				slideUp.start()
			}
		}
		super.onCreate(savedInstanceState)
		setContentView(layoutResId)
		val fm = supportFragmentManager
		var fragment = fm.findFragmentById(R.id.fragment_container)
		if (fragment == null) {
			fragment = LoginFragment()
			fm.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit()
		}
	}
}
