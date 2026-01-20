package com.wiley.fordummies.androidsdk.tictactoe.model

import androidx.annotation.Keep

/**
 * Class for reading settings from SharedPreferences (now the DataStore).
 *
 * Created by adamcchampion on 2017/08/14.
 */

@Keep
object Settings {
	const val NAME = "settings.db"
	const val ERROR = "error"

	object Keys {
		const val OPT_NAME = "name"
		const val OPT_NAME_DEFAULT = ""
		const val OPT_PLAY_FIRST = "human_starts"
		const val OPT_PLAY_FIRST_DEF = true

		const val SCOREPLAYERONEKEY = "ScorePlayerOne"
		const val SCOREPLAYERTWOKEY = "ScorePlayerTwo"
		const val GAMEKEY = "Game"

		const val PREF_SEARCH_QUERY = "searchQuery"
		const val PREF_LAST_RESULT_ID = "lastResultId"
		const val PREF_IS_POLLING = "isPolling"
	}
}
