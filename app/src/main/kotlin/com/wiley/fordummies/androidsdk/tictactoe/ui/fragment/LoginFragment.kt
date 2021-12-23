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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.preference.PreferenceManager
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccount
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.UserAccountViewModel
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.GameOptionsActivity
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Fragment for login screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */

class LoginFragment : Fragment(), View.OnClickListener {
	private lateinit var mUsernameEditText: EditText
	private lateinit var mPasswordEditText: EditText

	// private lateinit var mAccountSingleton: AccountSingleton
	// private lateinit var mDbHelper: AccountDbHelper

	private lateinit var mUserAccountViewModel: UserAccountViewModel

	private val TAG = javaClass.simpleName
	private val OPT_NAME = "name"


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v: View
		val activity = requireActivity()
		val rotation = activity.windowManager.defaultDisplay.rotation
		v = if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			inflater.inflate(R.layout.fragment_login_land, container, false)
		} else {
			inflater.inflate(R.layout.fragment_login, container, false)
		}

		mUsernameEditText = v.findViewById(R.id.username_text)
		mPasswordEditText = v.findViewById(R.id.password_text)

		val loginButton: Button = v.findViewById(R.id.login_button)
		loginButton.setOnClickListener(this)
		val cancelButton: Button = v.findViewById(R.id.cancel_button)
		cancelButton.setOnClickListener(this)
		val newUserButton: Button = v.findViewById(R.id.new_user_button)
		newUserButton.setOnClickListener(this)

		return v
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val activity = requireActivity() as AppCompatActivity
		mUserAccountViewModel = ViewModelProvider((activity as ViewModelStoreOwner)).get(
			UserAccountViewModel::class.java
		)
		// Here's a dummy observer object that indicates when the UserAccounts change in the database.
		mUserAccountViewModel.allUserAccounts.observe((activity as LifecycleOwner),
			{ userAccounts ->
				Timber.d(
					TAG,
					"The list of UserAccounts just changed; it has %s elements",
					userAccounts.size
				)
			})
		// mAccountSingleton = AccountSingleton.get(activity.applicationContext)
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
			// Old way to manage accounts
//			val accountList = mAccountSingleton.accounts
//			var hasMatchingAccount = false
//			for ((name, password1) in accountList) {
//				if (name == username && password1 == sha256HashStr) {
//					hasMatchingAccount = true
//					break
//				}
//			}

			/*
			 * This is the "old way" of querying the database directly for a matching Account.
			 * For the "new way", we would observe changes in the database via the ViewModel,
			 * then validate if the username/password combination is valid.
			 */
			/*if (mAccountSingleton == null) {
				mAccountSingleton = AccountSingleton.get(activity.getApplicationContext());
			}

			if (mDbHelper == null) {
				mDbHelper = new AccountDbHelper(activity.getApplicationContext());
			}

			List<Account> accountList = mAccountSingleton.getAccounts();
			boolean hasMatchingAccount = false;
			for (Account account : accountList) {
				if (account.getName().equals(username) && account.getPassword().equals(sha256HashStr)) {
					hasMatchingAccount = true;
					break;
				}
			}
			*/
			// if (accountList.size() > 0 && hasMatchingAccount) {
			val userAccount = UserAccount(username, sha256HashStr)
			val userAccountListData = mUserAccountViewModel.allUserAccounts
			val userAccountList = userAccountListData.value!!

			// if (accountList.size > 0 && hasMatchingAccount) {
			if (userAccountList.contains(userAccount)) {
				// Save username as the name of the player
				val settings =
					PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
				val editor = settings.edit()
				editor.putString(OPT_NAME, username)
				editor.apply()

				// Bring up the GameOptions screen
				startActivity(Intent(activity, GameOptionsActivity::class.java))
				activity.finish()
			} else {
				val manager = parentFragmentManager
				val fragment = LoginErrorDialogFragment()
				fragment.show(manager, "login_error")
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
