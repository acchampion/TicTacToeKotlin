package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * DialogFragment that shows "No network connectivity" error.
 *
 * Created by adamcchampion on 2017/08/14.
 */

class NoNetworkConnectionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.warning))
                .setMessage(resources.getString(R.string.no_net_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(resources.getString(R.string.ok_text)) { _, _ -> }
                .create()
    }
}
