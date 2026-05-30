package com.example.mypersonality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonality.core.model.ThemeMode
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.preferences.UserPreferencesRepository
import com.example.mypersonality.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            authRepository.restoreSessionIfNeeded()
        }
    }

    val session: StateFlow<UserSession?> = authRepository.currentSession.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemeMode.SYSTEM
    )

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
        }
    }
}
