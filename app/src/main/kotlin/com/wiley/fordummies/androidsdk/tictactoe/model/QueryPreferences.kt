package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.annotation.Keep
import androidx.core.content.edit
import androidx.preference.PreferenceManager

@Keep
object QueryPreferences {
	private const val PREF_SEARCH_QUERY = "searchQuery"
	private const val PREF_LAST_RESULT_ID = "lastResultId"
	private const val PREF_IS_POLLING = "isPolling"

	fun getStoredQuery(context: Context): String? {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.getString(PREF_SEARCH_QUERY, "")!!
	}

	fun setStoredQuery(context: Context, query: String?) {
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit {
				putString(PREF_SEARCH_QUERY, query)
			}
	}

	fun getLastResultId(context: Context): String? {
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getString(PREF_LAST_RESULT_ID, "")!!
	}

	fun setLastResultId(context: Context, lastResultId: String?) {
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit {
				putString(PREF_LAST_RESULT_ID, lastResultId)
			}
	}

	fun isPolling(context: Context): Boolean {
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(PREF_IS_POLLING, false)
	}

	fun setPolling(context: Context, isOn: Boolean) {
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putBoolean(PREF_IS_POLLING, isOn)
			.apply()
	}
}
