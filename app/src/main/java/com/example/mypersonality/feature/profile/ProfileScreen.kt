package com.example.mypersonality.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.ui.InfoRow
import com.example.mypersonality.core.ui.SectionTitle

@Composable
fun ProfileScreen(
    onThemeChanged: (Boolean) -> Unit,
    onApplicationsClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val session = state.session ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("Профиль")
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(session.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                InfoRow("Почта", session.email)
                InfoRow(
                    "Роль",
                    if (session.role == UserRole.SEEKER) "Соискатель" else "Работодатель"
                )
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Настройки", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Темная тема")
                    Switch(
                        checked = state.darkThemeEnabled,
                        onCheckedChange = onThemeChanged
                    )
                }
            }
        }

        if (session.role == UserRole.SEEKER) {
            Button(onClick = onApplicationsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Мои отклики")
            }
        }

        Button(onClick = viewModel::logout, modifier = Modifier.fillMaxWidth()) {
            Text("Выйти")
        }
    }
}
