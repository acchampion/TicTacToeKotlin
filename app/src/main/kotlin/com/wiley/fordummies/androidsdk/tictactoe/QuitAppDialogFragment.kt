package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * Created by adamcchampion on 2017/08/12.
 */

class QuitAppDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(activity?.resources?.getString(R.string.exit))
                .setMessage(activity?.resources?.getString(R.string.should_quit))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(activity?.resources?.getString(R.string.yes)) { _, _ -> System.exit(0) }
                .setNegativeButton(activity?.resources?.getString(R.string.no)) { _, _ -> }.create()
    }
}
