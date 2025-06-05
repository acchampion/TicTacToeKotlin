package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Created by adamcchampion on 2017/08/16.
 */
@Keep
class ReadImagePermissionDeniedDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(resources.getString(R.string.error))
                .setMessage(resources.getString(R.string.read_image_permission_denied))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(resources.getString(R.string.ok_text)
                    ) { _, _ -> requireActivity().finish() }
                .create()
    }

}
