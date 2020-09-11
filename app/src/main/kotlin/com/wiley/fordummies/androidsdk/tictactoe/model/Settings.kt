package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Class for reading settings from SharedPreferences.
 *
 * Created by adamcchampion on 2017/08/14.
 */

object Settings {
    private const val OPT_NAME = "name"
    private const val OPT_NAME_DEF = "Player"
    private const val OPT_PLAY_FIRST = "human_starts"
    private const val OPT_PLAY_FIRST_DEF = true

    fun getName(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_NAME, OPT_NAME_DEF)!!
    }

    fun doesHumanPlayFirst(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_PLAY_FIRST, OPT_PLAY_FIRST_DEF)
    }
}
