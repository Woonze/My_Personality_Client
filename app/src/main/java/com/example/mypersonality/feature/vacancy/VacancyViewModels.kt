package com.example.mypersonality.feature.vacancy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.model.VacancyApplication
import com.example.mypersonality.core.model.VacancyDraft
import com.example.mypersonality.data.auth.AuthRepository
import com.example.mypersonality.data.vacancy.VacancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VacancyDetailUiState(
    val vacancy: Vacancy? = null,
    val isLoading: Boolean = true,
    val isEmployer: Boolean = false,
    val isApplying: Boolean = false,
    val isDeleting: Boolean = false,
    val coverLetter: String = "",
    val message: String? = null,
    val errorMessage: String? = null
)

data class VacancyEditorUiState(
    val draft: VacancyDraft = VacancyDraft(),
    val isSaving: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

data class ApplicationsUiState(
    val isLoading: Boolean = true,
    val applications: List<VacancyApplication> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class VacancyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val vacancyRepository: VacancyRepository
) : ViewModel() {

    private val vacancyId: String = checkNotNull(savedStateHandle["vacancyId"])
    private val _uiState = MutableStateFlow(VacancyDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isEmployer = session.role == UserRole.EMPLOYER) }
            vacancyRepository.getVacancy(vacancyId, session).fold(
                onSuccess = { vacancy -> _uiState.update { it.copy(isLoading = false, vacancy = vacancy) } },
                onFailure = { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
            )
        }
    }

    fun updateCoverLetter(value: String) {
        _uiState.update { it.copy(coverLetter = value) }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            vacancyRepository.toggleFavorite(vacancyId, session)
            load()
        }
    }

    fun apply() {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isApplying = true, message = null, errorMessage = null) }
            vacancyRepository.applyToVacancy(vacancyId, uiState.value.coverLetter, session).fold(
                onSuccess = { _uiState.update { it.copy(isApplying = false, message = "Отклик отправлен") } },
                onFailure = { error -> _uiState.update { it.copy(isApplying = false, errorMessage = error.message) } }
            )
        }
    }

    fun deleteVacancy(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isDeleting = true, message = null, errorMessage = null) }
            vacancyRepository.deleteVacancy(vacancyId, session).fold(
                onSuccess = {
                    _uiState.update { it.copy(isDeleting = false, message = "Вакансия удалена") }
                    onDeleted()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isDeleting = false, errorMessage = error.message) }
                }
            )
        }
    }
}

@HiltViewModel
class VacancyEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val vacancyRepository: VacancyRepository
) : ViewModel() {

    private val vacancyId: String? = savedStateHandle["vacancyId"]
    private val _uiState = MutableStateFlow(VacancyEditorUiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        val currentId = vacancyId ?: return
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            vacancyRepository.getVacancy(currentId, session).onSuccess { vacancy ->
                _uiState.update {
                    it.copy(
                        draft = VacancyDraft(
                            id = vacancy.id,
                            title = vacancy.title,
                            companyName = vacancy.companyName,
                            city = vacancy.city,
                            salary = vacancy.salary,
                            employmentType = vacancy.employmentType,
                            description = vacancy.description
                        )
                    )
                }
            }
        }
    }

    fun updateDraft(transform: VacancyDraft.() -> VacancyDraft) {
        _uiState.update { it.copy(draft = it.draft.transform(), errorMessage = null, message = null) }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isSaving = true, errorMessage = null, message = null) }
            vacancyRepository.saveVacancy(uiState.value.draft, session).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, message = "Вакансия сохранена") }
                    onSaved()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
                }
            )
        }
    }
}

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val vacancyRepository: VacancyRepository
) : ViewModel() {

    private val vacancyId: String? = savedStateHandle["vacancyId"]
    private val _uiState = MutableStateFlow(ApplicationsUiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val session = authRepository.currentSession.value ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            vacancyRepository.getApplications(session, vacancyId).fold(
                onSuccess = { applications ->
                    _uiState.update { it.copy(isLoading = false, applications = applications) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
