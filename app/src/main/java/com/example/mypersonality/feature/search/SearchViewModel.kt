package com.example.mypersonality.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.preferences.UserPreferencesRepository
import com.example.mypersonality.data.auth.AuthRepository
import com.example.mypersonality.data.vacancy.VacancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val vacancies: List<Vacancy> = emptyList(),
    val errorMessage: String? = null,
    val history: List<String> = emptyList(),
    val showHistory: Boolean = false,
    val lastQuery: String = ""
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val vacancyRepository: VacancyRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val queryFlow = savedStateHandle.getStateFlow("query", "")
    private val _uiState = MutableStateFlow(SearchUiState())

    val uiState: StateFlow<SearchUiState> = combine(
        _uiState,
        preferencesRepository.searchHistory,
        queryFlow
    ) { uiState, history, query ->
        uiState.copy(query = query, history = history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    fun updateQuery(value: String) {
        savedStateHandle["query"] = value
        _uiState.update {
            it.copy(
                showHistory = value.isBlank() && it.history.isNotEmpty(),
                errorMessage = null
            )
        }
    }

    fun setHistoryVisible(visible: Boolean) {
        _uiState.update { state -> state.copy(showHistory = visible && state.history.isNotEmpty() && state.query.isBlank()) }
    }

    fun clearHistory() {
        viewModelScope.launch {
            preferencesRepository.clearHistory()
            _uiState.update { it.copy(showHistory = false) }
        }
    }

    fun useHistoryQuery(query: String) {
        updateQuery(query)
        search(query)
    }

    fun clearQuery() {
        updateQuery("")
        _uiState.update { it.copy(showHistory = false) }
    }

    fun search(query: String = uiState.value.query, favoritesOnly: Boolean = false) {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null, lastQuery = query, showHistory = false) }
            val result = if (favoritesOnly) {
                vacancyRepository.getFavoriteVacancies(session)
            } else {
                vacancyRepository.searchVacancies(query, session)
            }
            result.fold(
                onSuccess = { vacancies ->
                    if (!favoritesOnly) {
                        preferencesRepository.saveSearchQuery(query)
                    }
                    _uiState.update { it.copy(isLoading = false, vacancies = vacancies) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun retry(favoritesOnly: Boolean = false) {
        search(query = uiState.value.lastQuery, favoritesOnly = favoritesOnly)
    }
}
