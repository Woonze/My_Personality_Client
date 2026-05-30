package com.example.mypersonality.data.auth

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentSession: StateFlow<UserSession?>
    suspend fun restoreSessionIfNeeded()
    suspend fun register(email: String, password: String, fullName: String, role: UserRole): Result<UserSession>
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun logout()
}
