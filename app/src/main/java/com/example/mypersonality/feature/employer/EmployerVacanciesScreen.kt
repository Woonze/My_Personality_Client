package com.example.mypersonality.feature.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mypersonality.core.ui.EmptyPlaceholder
import com.example.mypersonality.core.ui.ErrorPlaceholder
import com.example.mypersonality.core.ui.SectionTitle
import com.example.mypersonality.core.ui.VacancyCard

@Composable
fun EmployerVacanciesScreen(
    onCreateVacancy: () -> Unit,
    onVacancyClick: (String) -> Unit,
    viewModel: EmployerVacanciesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("Мои вакансии")
        Button(onClick = onCreateVacancy, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.Icon(Icons.Outlined.AddCircle, contentDescription = null)
            androidx.compose.material3.Text(" Создать вакансию")
        }

        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> ErrorPlaceholder(
                message = state.errorMessage ?: "Ошибка",
                onRetry = viewModel::load
            )

            state.vacancies.isEmpty() -> EmptyPlaceholder(
                title = "Вакансий пока нет",
                subtitle = "Создайте первую вакансию, чтобы соискатели могли найти ее в поиске."
            )

            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.vacancies) { vacancy ->
                    VacancyCard(vacancy = vacancy, onOpen = { onVacancyClick(vacancy.id) })
                }
            }
        }
    }
}
