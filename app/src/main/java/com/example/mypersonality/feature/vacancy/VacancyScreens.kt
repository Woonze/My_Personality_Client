package com.example.mypersonality.feature.vacancy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mypersonality.core.ui.EmptyPlaceholder
import com.example.mypersonality.core.ui.ErrorPlaceholder
import com.example.mypersonality.core.ui.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyDetailScreen(
    onBack: () -> Unit,
    onApplicantsClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: VacancyDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали вакансии") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Column(modifier = Modifier.padding(20.dp)) {
                ErrorPlaceholder(message = state.errorMessage ?: "Ошибка", onRetry = viewModel::load)
            }

            state.vacancy != null -> {
                val vacancy = requireNotNull(state.vacancy)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(vacancy.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(vacancy.companyName, style = MaterialTheme.typography.titleMedium)
                            Text("${vacancy.city} • ${vacancy.salary}")
                        }
                    }
                    item {
                        InfoRow("Формат работы", vacancy.employmentType)
                    }
                    item {
                        Text("Описание", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(vacancy.description)
                    }
                    item {
                        if (state.isEmployer) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { onEditClick(vacancy.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Outlined.Edit, contentDescription = null)
                                    Text(" Редактировать")
                                }
                                Button(
                                    onClick = { viewModel.deleteVacancy(onDeleted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isDeleting
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null)
                                    Text(if (state.isDeleting) " Удаление..." else " Удалить вакансию")
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = viewModel::toggleFavorite, modifier = Modifier.fillMaxWidth()) {
                                    Icon(
                                        if (vacancy.isFavorite) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = null
                                    )
                                    Text(if (vacancy.isFavorite) " Убрать из избранного" else " В избранное")
                                }
                                OutlinedTextField(
                                    value = state.coverLetter,
                                    onValueChange = viewModel::updateCoverLetter,
                                    label = { Text("Сопроводительное письмо") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(onClick = viewModel::apply, modifier = Modifier.fillMaxWidth()) {
                                    Text(if (state.isApplying) "Отправка..." else "Откликнуться")
                                }
                            }
                        }
                    }
                    if (state.isEmployer) {
                        item {
                            Button(
                                onClick = { onApplicantsClick(vacancy.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Посмотреть отклики")
                            }
                        }
                    }
                    if (state.message != null) {
                        item {
                            Text(state.message ?: "", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (state.errorMessage != null) {
                        item {
                            Text(state.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyEditorScreen(
    onBack: () -> Unit,
    viewModel: VacancyEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вакансия") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.draft.title,
                onValueChange = { value -> viewModel.updateDraft { copy(title = value) } },
                label = { Text("Название вакансии") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.draft.companyName,
                onValueChange = { value -> viewModel.updateDraft { copy(companyName = value) } },
                label = { Text("Компания") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.draft.city,
                onValueChange = { value -> viewModel.updateDraft { copy(city = value) } },
                label = { Text("Город") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.draft.salary,
                onValueChange = { value -> viewModel.updateDraft { copy(salary = value) } },
                label = { Text("Зарплата") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.draft.employmentType,
                onValueChange = { value -> viewModel.updateDraft { copy(employmentType = value) } },
                label = { Text("Тип занятости") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.draft.description,
                onValueChange = { value -> viewModel.updateDraft { copy(description = value) } },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.save(onBack) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.isSaving) "Сохранение..." else "Сохранить")
            }
            state.message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    onBack: () -> Unit,
    employerMode: Boolean = false,
    viewModel: ApplicationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (employerMode) "Отклики на вакансию" else "Мои отклики") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Column(modifier = Modifier.padding(20.dp)) {
                ErrorPlaceholder(message = state.errorMessage ?: "Ошибка", onRetry = viewModel::load)
            }

            state.applications.isEmpty() -> Column(modifier = Modifier.padding(20.dp)) {
                EmptyPlaceholder(
                    title = "Список пуст",
                    subtitle = if (employerMode) "Пока никто не откликнулся на эту вакансию." else "Вы еще не отправляли отклики."
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.applications) { application ->
                    androidx.compose.material3.Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(application.vacancyTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (employerMode) "Кандидат: ${application.applicantName}" else "Статус: ${application.status}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(application.coverLetter, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
