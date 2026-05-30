package com.example.mypersonality.data.auth

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.preferences.UserPreferencesRepository
import com.example.mypersonality.data.remote.BackendApiService
import com.example.mypersonality.data.remote.LoginRequestDto
import com.example.mypersonality.data.remote.RegisterRequestDto
import com.example.mypersonality.data.remote.toUserSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class ServerAuthRepository @Inject constructor(
    private val backendApiService: BackendApiService,
    private val preferencesRepository: UserPreferencesRepository
) : AuthRepository {

    private val _currentSession = MutableStateFlow<UserSession?>(null)

    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun restoreSessionIfNeeded() {
        _currentSession.value = preferencesRepository.getSavedSession()
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: UserRole
    ): Result<UserSession> = runCatching {
        require(email.contains("@")) { "Укажите корректную почту" }
        require(password.length >= 6) { "Пароль должен быть не короче 6 символов" }
        require(fullName.isNotBlank()) { "Укажите имя пользователя" }

        val session = backendApiService.register(
            RegisterRequestDto(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = role
            )
        ).profile.toUserSession()
        persistSession(session)
        session
    }

    override suspend fun login(email: String, password: String): Result<UserSession> = runCatching {
        require(email.contains("@")) { "Укажите корректную почту" }
        require(password.isNotBlank()) { "Введите пароль" }

        val session = backendApiService.login(
            LoginRequestDto(
                email = email.trim(),
                password = password
            )
        ).profile.toUserSession()
        persistSession(session)
        session
    }

    override suspend fun logout() {
        _currentSession.value = null
        preferencesRepository.clearSession()
    }

    private suspend fun persistSession(session: UserSession) {
        preferencesRepository.saveSession(session)
        _currentSession.value = session
    }
}
