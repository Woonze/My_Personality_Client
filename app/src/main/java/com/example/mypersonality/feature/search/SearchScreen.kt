package com.example.mypersonality.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mypersonality.core.ui.EmptyPlaceholder
import com.example.mypersonality.core.ui.ErrorPlaceholder
import com.example.mypersonality.core.ui.SectionTitle
import com.example.mypersonality.core.ui.VacancyCard

@Composable
fun SearchScreen(
    onVacancyClick: (String) -> Unit,
    favoritesOnly: Boolean,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var fieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(favoritesOnly) {
        viewModel.search(favoritesOnly = favoritesOnly)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(if (favoritesOnly) "Избранные вакансии" else "Поиск вакансий")
        if (!favoritesOnly) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { fieldFocused = it.isFocused },
                placeholder = { Text("Введите вакансию, компанию или навык") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotBlank()) {
                        IconButton(
                            onClick = {
                                viewModel.clearQuery()
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(Icons.Outlined.Clear, contentDescription = "Очистить")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.search()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            )
            LaunchedEffect(fieldFocused, state.query, state.history) {
                viewModel.setHistoryVisible(fieldFocused)
            }
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                ErrorPlaceholder(
                    message = state.errorMessage ?: "Неизвестная ошибка",
                    onRetry = { viewModel.retry(favoritesOnly) }
                )
            }

            !favoritesOnly && state.showHistory -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("История поиска", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Очистить историю",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = viewModel::clearHistory)
                        )
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.history) { item ->
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.useHistoryQuery(item) }
                                    .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            state.vacancies.isEmpty() -> {
                EmptyPlaceholder(
                    title = if (favoritesOnly) "Избранного пока нет" else "Ничего не найдено",
                    subtitle = if (favoritesOnly) {
                        "Добавьте вакансии в избранное, и они появятся здесь."
                    } else {
                        "Попробуйте изменить запрос или запустить поиск позже."
                    }
                )
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.vacancies) { vacancy ->
                        VacancyCard(vacancy = vacancy, onOpen = { onVacancyClick(vacancy.id) })
                    }
                }
            }
        }
    }
}
