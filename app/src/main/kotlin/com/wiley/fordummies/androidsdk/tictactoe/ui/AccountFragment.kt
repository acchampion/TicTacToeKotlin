package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.model.Account
import com.wiley.fordummies.androidsdk.tictactoe.model.AccountSingleton
import timber.log.Timber

/**
 * Fragment for user account creation.
 *
 * Created by adamcchampion on 2017/08/05.
 */
class AccountFragment : Fragment(), View.OnClickListener {
    private lateinit var mEtUsername: EditText
    private lateinit var mEtPassword: EditText
    private lateinit var mEtConfirm: EditText

    // private val TAG = AccountFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        val username = mEtUsername.text.toString()
        val password = mEtPassword.text.toString()
        val confirm = mEtConfirm.text.toString()
		val activity = requireActivity()
        if (password == confirm && username != "" && password != "" && confirm != "") {
            val singleton = AccountSingleton.get(activity.applicationContext)
            val account = Account(username, password)
            singleton.addAccount(account)
            Toast.makeText(activity.applicationContext, "New record inserted", Toast.LENGTH_SHORT).show()
        } else if (username == "" || password == "" || confirm == "") {
            Toast.makeText(activity.applicationContext, "Missing entry", Toast.LENGTH_SHORT).show()
        } else if (password != confirm) {
            val manager = parentFragmentManager
            val fragment = AccountErrorDialogFragment()
			fragment.show(manager, "account_error")
        } else {
            Timber.e("An unknown account creation error occurred.")
        }
    }
}
