package com.example.mypersonality.feature.profile

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val session: UserSession? = null,
    val darkThemeEnabled: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.currentSession,
        preferencesRepository.themeMode
    ) { session, themeMode ->
        ProfileUiState(
            session = session,
            darkThemeEnabled = themeMode == ThemeMode.DARK
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
