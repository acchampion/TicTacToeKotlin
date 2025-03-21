package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccount
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.UserAccountViewModel
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.GameOptionsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Fragment for login screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */
@Keep
class LoginFragment : Fragment(), View.OnClickListener {
	private lateinit var mHeaderTextView: TextView
	private lateinit var mUsernameEditText: EditText
	private lateinit var mPasswordEditText: EditText
	private lateinit var mLoginButton: Button
	private lateinit var mCancelButton: Button
	private lateinit var mNewUserButton: Button

	private val mUserAccountViewModel: UserAccountViewModel by viewModels()

	private lateinit var mDataStore: SettingsDataStore
	private var mUserAccountList = CopyOnWriteArrayList<UserAccount>()

	private val classTag = javaClass.simpleName

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val activity = requireActivity()
		val rotation = activity.windowManager.defaultDisplay.rotation
		val v: View = inflater.inflate(R.layout.fragment_login, container, false)

		mDataStore = TicTacToeApplication.getDataStore()

		mHeaderTextView = v.findViewById(R.id.login_header)
		mUsernameEditText = v.findViewById(R.id.username_text)
		mPasswordEditText = v.findViewById(R.id.password_text)

		mLoginButton = v.findViewById(R.id.login_button)
		mLoginButton.setOnClickListener(this)
		mCancelButton = v.findViewById(R.id.cancel_button)
		mCancelButton.setOnClickListener(this)

		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			mNewUserButton = v.findViewById(R.id.new_user_button)
			mNewUserButton.setOnClickListener(this)
		}

		return v
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val activity = requireActivity() as AppCompatActivity

		// Here's a dummy observer object that indicates when the UserAccounts change in the database.
		mUserAccountViewModel.allUserAccounts.observe(
			(activity as LifecycleOwner)
		) { userAccounts ->
			Timber.tag(classTag)
				.d("The list of UserAccounts just changed; it has %s elements", userAccounts.size)
			mUserAccountList.clear()
			mUserAccountList.addAll(userAccounts)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}


	override fun onDestroyView() {
		super.onDestroyView()
		Timber.tag(classTag).d("onDestroyView()")
	}

	override fun onDestroy() {
		super.onDestroy()
		val activity = requireActivity()
		mUserAccountViewModel.allUserAccounts.removeObservers(activity as LifecycleOwner)
	}


	private fun checkLogin() {
		val username = mUsernameEditText.text.toString()
		val password = mPasswordEditText.text.toString()
		val digest: MessageDigest
		try {
			digest = MessageDigest.getInstance("SHA-256")
			val sha256HashBytes = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
			val sha256HashStr = StringUtils.bytesToHex(sha256HashBytes)
			val activity: Activity = requireActivity()

			val userAccount = UserAccount(username, sha256HashStr)

			// if (accountList.size > 0 && hasMatchingAccount) {
			if (mUserAccountList.contains(userAccount)) {
				CoroutineScope(Dispatchers.IO).launch {
					mDataStore.putString(Settings.Keys.OPT_NAME, username)
					Timber.tag(classTag).d("Wrote username successfully to DataStore")
				}

				// Bring up the GameOptions screen
				startActivity(Intent(activity, GameOptionsActivity::class.java))
				activity.finish()
			} else {
				showLoginErrorFragment()
			}
		} catch (e: NoSuchAlgorithmException) {
			e.printStackTrace()
		}
	}


	override fun onResume() {
		super.onResume()
		val activity = requireActivity() as AppCompatActivity
		activity.supportActionBar?.apply {
			subtitle = resources.getString(R.string.account)
		}
	}

	private fun showLoginErrorFragment() {
		val manager: FragmentManager = parentFragmentManager
		val fragment = LoginErrorDialogFragment()
		fragment.show(manager, "login_error")
	}

	override fun onClick(view: View) {
		val activity = requireActivity()
		when (view.id) {
			R.id.login_button -> checkLogin()
			R.id.cancel_button -> activity.finish()
			R.id.new_user_button -> {
				val rotation = activity.windowManager.defaultDisplay.rotation
				val fm = parentFragmentManager
				val fragment = AccountFragment()
				if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
					fm.beginTransaction()
						.replace(R.id.fragment_container, fragment)
						.addToBackStack("account_fragment")
						.commit()
				} else {
					fm.beginTransaction()
						.add(R.id.account_fragment_container, fragment)
						.addToBackStack("account_fragment")
						.commit()
				}
			}
		}
	}
}
