package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * DialogFragment that gives user login error.
 *
 * Created by adamcchampion on 2017/08/04.
 */

class LoginErrorDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.login_error_text))
                .setPositiveButton(resources.getString(R.string.ok_text)) { _, _ -> }.create()
    }
}
