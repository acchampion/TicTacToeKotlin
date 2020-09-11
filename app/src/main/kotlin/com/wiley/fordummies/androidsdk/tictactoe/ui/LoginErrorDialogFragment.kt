package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * DialogFragment that gives user login error.
 *
 * Created by adamcchampion on 2017/08/04.
 */

class LoginErrorDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.login_error_text))
                .setPositiveButton(resources.getString(R.string.ok_text)) { _, _ -> }.create()
    }
}
