package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * DialogFragment that tells user that location permissions were denied.
 *
 * Created by adamcchampion on 2017/08/17.
 */

class LocationDeniedDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.location_permission_denied))
                .setPositiveButton(resources.getString(R.string.ok_text)) { _, _ -> activity?.finish() }.create()
    }
}
