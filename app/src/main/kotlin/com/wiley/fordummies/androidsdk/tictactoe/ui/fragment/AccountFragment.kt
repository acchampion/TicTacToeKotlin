package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccount
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.UserAccountViewModel
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Fragment for user account creation.
 *
 * Created by adamcchampion on 2017/08/05.
 */
class AccountFragment : Fragment(), View.OnClickListener {
	private lateinit var mEtUsername: EditText
	private lateinit var mEtPassword: EditText
	private lateinit var mEtConfirm: EditText
	private val mUserAccountViewModel: UserAccountViewModel by viewModels()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_account, container, false)

		mEtUsername = v.findViewById(R.id.username)
		mEtPassword = v.findViewById(R.id.password)
		mEtConfirm = v.findViewById(R.id.password_confirm)
		val btnAdd: Button = v.findViewById(R.id.done_button)
		btnAdd.setOnClickListener(this)
		val btnCancel: Button = v.findViewById(R.id.cancel_button)
		btnCancel.setOnClickListener(this)

		return v
	}

	override fun onResume() {
		super.onResume()
		val activity = requireActivity() as AppCompatActivity
		activity.supportActionBar?.apply {
			subtitle = resources.getString(R.string.account)
		}
	}

	override fun onClick(view: View) {
		when (view.id) {
			R.id.done_button -> createAccount()
			R.id.cancel_button -> {
				mEtUsername.setText("")
				mEtPassword.setText("")
				mEtConfirm.setText("")
			}
		}
	}

	private fun createAccount() {
		val activity = requireActivity()
		val username = mEtUsername.text.toString()
		val password = mEtPassword.text.toString()
		val confirm = mEtConfirm.text.toString()
		if (password == confirm && username != "" && password != "") {
			// Old way: use account singleton
			// val singleton = AccountSingleton.get(activity.applicationContext)
			try {
				val digest = MessageDigest.getInstance("SHA-256")
				val sha256HashBytes = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
				val sha256HashStr = StringUtils.bytesToHex(sha256HashBytes)

				// New way: create new UserAccount, then add it to ViewModel
				val userAccount = UserAccount(username, sha256HashStr)
				mUserAccountViewModel.insert(userAccount)
				Toast.makeText(
					activity.applicationContext,
					"New UserAccount added",
					Toast.LENGTH_SHORT
				).show()

			} catch (e: NoSuchAlgorithmException) {
				Toast.makeText(activity, "Error: No SHA-256 algorithm found", Toast.LENGTH_SHORT)
					.show()
				e.printStackTrace()
			}
		} else if (username == "" || password == "" || confirm == "") {
			Toast.makeText(activity.applicationContext, "Missing entry", Toast.LENGTH_SHORT).show()
		} else {
			Timber.e("An unknown account creation error occurred.")
			val manager = parentFragmentManager
			val fragment = AccountErrorDialogFragment()
			fragment.show(manager, "account_error")
		}
	}
}
