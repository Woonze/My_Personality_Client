package com.example.mypersonality.feature.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.data.auth.AuthRepository
import com.example.mypersonality.data.vacancy.VacancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployerVacanciesUiState(
    val isLoading: Boolean = true,
    val vacancies: List<Vacancy> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class EmployerVacanciesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vacancyRepository: VacancyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerVacanciesUiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            vacancyRepository.getEmployerVacancies(session).fold(
                onSuccess = { vacancies -> _uiState.update { it.copy(isLoading = false, vacancies = vacancies) } },
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
            )
        }
    }
}
