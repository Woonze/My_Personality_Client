package com.example.mypersonality.data.auth

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class InMemoryAuthRepository @Inject constructor() : AuthRepository {

    private val sessions = mutableMapOf<String, UserSession>()
    private val _currentSession = MutableStateFlow<UserSession?>(null)

    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun restoreSessionIfNeeded() = Unit

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: UserRole
    ): Result<UserSession> {
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Укажите корректную почту"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Пароль должен быть не короче 6 символов"))
        val session = sessions[email] ?: UserSession(
            id = UUID.randomUUID().toString(),
            email = email,
            fullName = fullName,
            role = role
        )
        sessions[email] = session
        _currentSession.value = session
        return Result.success(session)
    }

    override suspend fun login(email: String, password: String): Result<UserSession> {
        val session = sessions[email] ?: return Result.failure(IllegalArgumentException("Пользователь не найден"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Неверный пароль"))
        _currentSession.value = session
        return Result.success(session)
    }

    override suspend fun logout() {
        _currentSession.value = null
    }
}
