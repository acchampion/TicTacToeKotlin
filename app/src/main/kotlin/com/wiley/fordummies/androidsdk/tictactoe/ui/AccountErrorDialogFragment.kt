package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * DialogFragment that is shown when username and password(s) do not match.
 *
 * Created by adamcchampion on 2017/08/05.
 */

class AccountErrorDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.passwords_match_error_text))
                .setPositiveButton(resources.getString(R.string.try_again_text)) { _, _ -> }.create()
    }
}
