package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Created by adamcchampion on 2017/08/16.
 */

class ContactPermissionDeniedDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.read_contacts_permission_denied))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(resources.getString(R.string.ok_text)
                    ) { dialog, which -> activity?.finish() }
                .create()
    }

}
