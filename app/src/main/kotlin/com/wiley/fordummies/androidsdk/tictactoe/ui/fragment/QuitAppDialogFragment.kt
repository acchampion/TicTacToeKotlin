package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import kotlin.system.exitProcess

/**
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class QuitAppDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(activity?.resources?.getString(R.string.exit))
                .setMessage(activity?.resources?.getString(R.string.should_quit))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(activity?.resources?.getString(R.string.yes)) { _, _ -> exitProcess(0) }
                .setNegativeButton(activity?.resources?.getString(R.string.no)) { _, _ -> }.create()
    }
}
