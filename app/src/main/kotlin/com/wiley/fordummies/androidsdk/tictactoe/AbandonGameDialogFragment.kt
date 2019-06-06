package com.wiley.fordummies.androidsdk.tictactoe

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * DialogFragment asking if user should abandon Tic-Tac-Toe game.
 *
 * Created by adamcchampion on 2017/08/20.
 */

class AbandonGameDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.exit))
                .setMessage(resources.getString(R.string.abandon_game))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which -> activity?.finish() }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, which -> }.create()
    }
}
