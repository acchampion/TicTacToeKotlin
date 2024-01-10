package com.wiley.fordummies.androidsdk.tictactoe.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsDataStore(val context: Context) {
	val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Settings.NAME)

	suspend fun getInt(key: String, value: Int): Int {
		val prefKey = intPreferencesKey(key)
		return context.dataStore.data
			.map { preferences -> preferences[prefKey] ?: 0 }
			.first()
	}

	suspend fun putInt(key: String, value: Int) {
		val prefKey = intPreferencesKey(key)
		context.dataStore.edit { preferences ->
			preferences[prefKey] = value
		}
	}

	suspend fun getString(key: String, value: String): String {
		val prefKey = stringPreferencesKey(key)
		return context.dataStore.data
			.map { preferences -> preferences[prefKey] ?: "" }
			.first()
	}

	suspend fun putString(key: String, value: String) {
		val prefKey = stringPreferencesKey(key)
		context.dataStore.edit { preferences ->
			preferences[prefKey] = value
		}
	}

	suspend fun getBoolean(key: String, value: Boolean): Boolean {
		val prefKey = booleanPreferencesKey(key)
		return context.dataStore.data
			.map { preferences -> preferences[prefKey] ?: false }
			.first()
	}

	suspend fun putBoolean(key: String, value: Boolean) {
		val prefKey = booleanPreferencesKey(key)
		context.dataStore.edit { preferences ->
			preferences[prefKey] = value
		}
	}
}
