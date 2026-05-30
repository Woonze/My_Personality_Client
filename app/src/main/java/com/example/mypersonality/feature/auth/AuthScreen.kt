package com.example.mypersonality.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mypersonality.core.model.UserRole

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthContent(
        state = state,
        onNameChanged = viewModel::updateFullName,
        onEmailChanged = viewModel::updateEmail,
        onPasswordChanged = viewModel::updatePassword,
        onRoleChanged = viewModel::updateRole,
        onModeChanged = viewModel::updateMode,
        onSubmit = viewModel::submit
    )
}

@Composable
fun AuthContent(
    state: AuthUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRoleChanged: (UserRole) -> Unit,
    onModeChanged: (AuthMode) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Подбор персонала", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Войдите по почте и паролю. Для новой регистрации выберите роль заранее.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.mode == AuthMode.REGISTER,
                        onClick = { onModeChanged(AuthMode.REGISTER) },
                        label = { Text("Регистрация") }
                    )
                    FilterChip(
                        selected = state.mode == AuthMode.LOGIN,
                        onClick = { onModeChanged(AuthMode.LOGIN) },
                        label = { Text("Вход") }
                    )
                }
                if (state.mode == AuthMode.REGISTER) {
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = onNameChanged,
                        label = { Text("Имя и фамилия") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    label = { Text("Электронная почта") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChanged,
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (state.mode == AuthMode.REGISTER) {
                    Text("Роль", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    listOf(UserRole.SEEKER to "Соискатель", UserRole.EMPLOYER to "Работодатель").forEach { (role, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.selectedRole == role,
                                    onClick = { onRoleChanged(role) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = state.selectedRole == role, onClick = { onRoleChanged(role) })
                            Text(label)
                        }
                    }
                }

                Button(
                    onClick = onSubmit,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(if (state.mode == AuthMode.REGISTER) "Зарегистрироваться" else "Войти")
                    }
                }

                Text(
                    text = state.errorMessage ?: state.helperText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.errorMessage == null) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}
