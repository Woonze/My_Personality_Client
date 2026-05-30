package com.example.mypersonality.data.vacancy

import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.model.VacancyApplication
import com.example.mypersonality.core.model.VacancyDraft

interface VacancyRepository {
    suspend fun searchVacancies(query: String, userSession: UserSession): Result<List<Vacancy>>
    suspend fun getFavoriteVacancies(userSession: UserSession): Result<List<Vacancy>>
    suspend fun getEmployerVacancies(userSession: UserSession): Result<List<Vacancy>>
    suspend fun getVacancy(vacancyId: String, userSession: UserSession): Result<Vacancy>
    suspend fun toggleFavorite(vacancyId: String, userSession: UserSession): Result<Unit>
    suspend fun applyToVacancy(vacancyId: String, coverLetter: String, userSession: UserSession): Result<Unit>
    suspend fun getApplications(userSession: UserSession, vacancyId: String? = null): Result<List<VacancyApplication>>
    suspend fun saveVacancy(draft: VacancyDraft, userSession: UserSession): Result<Unit>
    suspend fun deleteVacancy(vacancyId: String, userSession: UserSession): Result<Unit>
}
