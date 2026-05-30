package com.example.mypersonality.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    REGISTER
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.REGISTER,
    val email: String = "",
    val fullName: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.SEEKER,
    val isLoading: Boolean = false,
    val helperText: String = "Войдите по почте и паролю. При регистрации заранее выберите роль.",
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) = updateState { copy(email = value, errorMessage = null) }
    fun updateFullName(value: String) = updateState { copy(fullName = value, errorMessage = null) }
    fun updatePassword(value: String) = updateState { copy(password = value, errorMessage = null) }
    fun updateRole(role: UserRole) = updateState { copy(selectedRole = role) }
    fun updateMode(mode: AuthMode) = updateState { copy(mode = mode, errorMessage = null) }

    fun submit() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            val operation = if (uiState.value.mode == AuthMode.REGISTER) {
                authRepository.register(
                    email = uiState.value.email,
                    password = uiState.value.password,
                    fullName = uiState.value.fullName,
                    role = uiState.value.selectedRole
                )
            } else {
                authRepository.login(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            }
            operation.fold(
                onSuccess = {
                    updateState { copy(isLoading = false, password = "") }
                },
                onFailure = { error ->
                    updateState { copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    private fun updateState(transform: AuthUiState.() -> AuthUiState) {
        _uiState.update(transform)
    }
}
