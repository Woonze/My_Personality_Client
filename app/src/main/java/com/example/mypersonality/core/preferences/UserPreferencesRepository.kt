package com.example.mypersonality.core.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mypersonality.core.model.ThemeMode
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.entries.firstOrNull { it.name == prefs[PreferenceKeys.THEME_MODE] } ?: ThemeMode.SYSTEM
    }

    val searchHistory: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.SEARCH_HISTORY]
            ?.split("||")
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[PreferenceKeys.THEME_MODE] = mode.name }
    }

    suspend fun saveSearchQuery(query: String) {
        if (query.isBlank()) return
        context.dataStore.edit { prefs ->
            val updated = buildList {
                add(query.trim())
                addAll(
                    prefs[PreferenceKeys.SEARCH_HISTORY]
                        ?.split("||")
                        ?.filter { it.isNotBlank() && !it.equals(query.trim(), ignoreCase = true) }
                        .orEmpty()
                )
            }.take(10)
            prefs[PreferenceKeys.SEARCH_HISTORY] = updated.joinToString("||")
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs -> prefs.remove(PreferenceKeys.SEARCH_HISTORY) }
    }

    suspend fun saveSession(session: UserSession) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SESSION_ID] = session.id
            prefs[PreferenceKeys.SESSION_EMAIL] = session.email
            prefs[PreferenceKeys.SESSION_FULL_NAME] = session.fullName
            prefs[PreferenceKeys.SESSION_ROLE] = session.role.name
        }
    }

    suspend fun getSavedSession(): UserSession? = context.dataStore.data.map { prefs ->
        val id = prefs[PreferenceKeys.SESSION_ID]
        val email = prefs[PreferenceKeys.SESSION_EMAIL]
        val fullName = prefs[PreferenceKeys.SESSION_FULL_NAME]
        val roleName = prefs[PreferenceKeys.SESSION_ROLE]
        if (id.isNullOrBlank() || email.isNullOrBlank() || fullName.isNullOrBlank() || roleName.isNullOrBlank()) {
            null
        } else {
            UserSession(
                id = id,
                email = email,
                fullName = fullName,
                role = UserRole.valueOf(roleName)
            )
        }
    }.first()

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.SESSION_ID)
            prefs.remove(PreferenceKeys.SESSION_EMAIL)
            prefs.remove(PreferenceKeys.SESSION_FULL_NAME)
            prefs.remove(PreferenceKeys.SESSION_ROLE)
        }
    }

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
        val SESSION_ID = stringPreferencesKey("session_id")
        val SESSION_EMAIL = stringPreferencesKey("session_email")
        val SESSION_FULL_NAME = stringPreferencesKey("session_full_name")
        val SESSION_ROLE = stringPreferencesKey("session_role")
    }
}
