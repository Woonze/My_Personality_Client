package com.example.mypersonality.data.vacancy

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.model.VacancyApplication
import com.example.mypersonality.core.model.VacancyDraft
import com.example.mypersonality.data.remote.ApplicationRequestDto
import com.example.mypersonality.data.remote.BackendApiService
import com.example.mypersonality.data.remote.toModel
import com.example.mypersonality.data.remote.toRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitVacancyRepository @Inject constructor(
    private val backendApiService: BackendApiService
) : VacancyRepository {

    override suspend fun searchVacancies(query: String, userSession: UserSession): Result<List<Vacancy>> = runCatching {
        val favorites = backendApiService.getFavorites(userSession.id).map { it.id }.toSet()
        backendApiService.searchVacancies(query).map { it.toModel(isFavorite = it.id in favorites) }
    }

    override suspend fun getFavoriteVacancies(userSession: UserSession): Result<List<Vacancy>> = runCatching {
        backendApiService.getFavorites(userSession.id).map { it.toModel(isFavorite = true) }
    }

    override suspend fun getEmployerVacancies(userSession: UserSession): Result<List<Vacancy>> = runCatching {
        require(userSession.role == UserRole.EMPLOYER) { "Раздел доступен только работодателю" }
        backendApiService.getEmployerVacancies(userSession.id).map { it.toModel() }
    }

    override suspend fun getVacancy(vacancyId: String, userSession: UserSession): Result<Vacancy> = runCatching {
        val favorites = backendApiService.getFavorites(userSession.id).map { it.id }.toSet()
        backendApiService.getVacancy(vacancyId).toModel(isFavorite = vacancyId in favorites)
    }

    override suspend fun toggleFavorite(vacancyId: String, userSession: UserSession): Result<Unit> = runCatching {
        backendApiService.toggleFavorite(vacancyId, userSession.id)
        Unit
    }

    override suspend fun applyToVacancy(
        vacancyId: String,
        coverLetter: String,
        userSession: UserSession
    ): Result<Unit> = runCatching {
        backendApiService.applyToVacancy(
            vacancyId = vacancyId,
            userId = userSession.id,
            request = ApplicationRequestDto(vacancyId = vacancyId, coverLetter = coverLetter)
        )
        Unit
    }

    override suspend fun getApplications(
        userSession: UserSession,
        vacancyId: String?
    ): Result<List<VacancyApplication>> = runCatching {
        if (userSession.role == UserRole.EMPLOYER && vacancyId != null) {
            backendApiService.getVacancyApplications(vacancyId).map { it.toModel() }
        } else {
            backendApiService.getSeekerApplications(userSession.id).map { it.toModel() }
        }
    }

    override suspend fun saveVacancy(draft: VacancyDraft, userSession: UserSession): Result<Unit> = runCatching {
        if (draft.id == null) {
            backendApiService.createVacancy(userSession.id, draft.toRequestDto())
        } else {
            backendApiService.updateVacancy(draft.id, userSession.id, draft.toRequestDto())
        }
        Unit
    }

    override suspend fun deleteVacancy(vacancyId: String, userSession: UserSession): Result<Unit> = runCatching {
        require(userSession.role == UserRole.EMPLOYER) { "Удалять вакансии может только работодатель" }
        backendApiService.deleteVacancy(vacancyId, userSession.id)
        Unit
    }
}
