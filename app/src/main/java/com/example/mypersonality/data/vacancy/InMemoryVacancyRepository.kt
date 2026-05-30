package com.example.mypersonality.data.vacancy

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.model.VacancyApplication
import com.example.mypersonality.core.model.VacancyDraft
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryVacancyRepository @Inject constructor() : VacancyRepository {

    private val favorites = mutableMapOf<String, MutableSet<String>>()
    private val vacancies = mutableListOf(
        Vacancy(
            id = "v1",
            employerId = "employer-demo",
            companyName = "ООО Старт Карьера",
            title = "Junior Android Developer",
            city = "Москва",
            salary = "от 90 000 ₽",
            employmentType = "Полный день",
            description = "Разработка клиентского приложения на Kotlin, работа с Compose, REST API и командой backend."
        ),
        Vacancy(
            id = "v2",
            employerId = "employer-demo",
            companyName = "HR Tech Lab",
            title = "QA Engineer",
            city = "Санкт-Петербург",
            salary = "от 75 000 ₽",
            employmentType = "Гибрид",
            description = "Тестирование мобильного приложения, чек-листы, smoke и регрессионные проверки."
        ),
        Vacancy(
            id = "v3",
            employerId = "employer-city",
            companyName = "Город Вакансий",
            title = "UI/UX Designer",
            city = "Казань",
            salary = "от 110 000 ₽",
            employmentType = "Удаленно",
            description = "Проектирование экранов мобильного приложения, пользовательские сценарии и дизайн-система."
        )
    )
    private val applications = mutableListOf<VacancyApplication>()

    override suspend fun searchVacancies(query: String, userSession: UserSession): Result<List<Vacancy>> {
        val filtered = vacancies.filter { vacancy ->
            query.isBlank() ||
                vacancy.title.contains(query, ignoreCase = true) ||
                vacancy.description.contains(query, ignoreCase = true) ||
                vacancy.companyName.contains(query, ignoreCase = true)
        }.map { vacancy ->
            vacancy.copy(isFavorite = favorites[userSession.id]?.contains(vacancy.id) == true)
        }
        return Result.success(filtered)
    }

    override suspend fun getFavoriteVacancies(userSession: UserSession): Result<List<Vacancy>> {
        val favoriteIds = favorites[userSession.id].orEmpty()
        return Result.success(
            vacancies.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) }
        )
    }

    override suspend fun getEmployerVacancies(userSession: UserSession): Result<List<Vacancy>> {
        if (userSession.role != UserRole.EMPLOYER) {
            return Result.failure(IllegalStateException("Раздел доступен только работодателю"))
        }
        return Result.success(vacancies.filter { it.employerId == userSession.id })
    }

    override suspend fun getVacancy(vacancyId: String, userSession: UserSession): Result<Vacancy> {
        val vacancy = vacancies.firstOrNull { it.id == vacancyId }
            ?: return Result.failure(NoSuchElementException("Вакансия не найдена"))
        return Result.success(vacancy.copy(isFavorite = favorites[userSession.id]?.contains(vacancyId) == true))
    }

    override suspend fun toggleFavorite(vacancyId: String, userSession: UserSession): Result<Unit> {
        val userFavorites = favorites.getOrPut(userSession.id) { mutableSetOf() }
        if (!userFavorites.add(vacancyId)) {
            userFavorites.remove(vacancyId)
        }
        return Result.success(Unit)
    }

    override suspend fun applyToVacancy(
        vacancyId: String,
        coverLetter: String,
        userSession: UserSession
    ): Result<Unit> {
        val vacancy = vacancies.firstOrNull { it.id == vacancyId }
            ?: return Result.failure(NoSuchElementException("Вакансия не найдена"))
        if (applications.any { it.vacancyId == vacancyId && it.applicantId == userSession.id }) {
            return Result.failure(IllegalStateException("Вы уже откликались на эту вакансию"))
        }
        applications += VacancyApplication(
            id = UUID.randomUUID().toString(),
            vacancyId = vacancyId,
            vacancyTitle = vacancy.title,
            applicantId = userSession.id,
            applicantName = userSession.fullName,
            employerId = vacancy.employerId,
            coverLetter = coverLetter.ifBlank { "Без сопроводительного письма" },
            status = "Отправлен"
        )
        return Result.success(Unit)
    }

    override suspend fun getApplications(
        userSession: UserSession,
        vacancyId: String?
    ): Result<List<VacancyApplication>> {
        val filtered = if (userSession.role == UserRole.EMPLOYER) {
            applications.filter { vacancyId == null || it.vacancyId == vacancyId }
        } else {
            applications.filter { it.applicantId == userSession.id }
        }
        return Result.success(filtered)
    }

    override suspend fun saveVacancy(draft: VacancyDraft, userSession: UserSession): Result<Unit> {
        if (userSession.role != UserRole.EMPLOYER) {
            return Result.failure(IllegalStateException("Создавать вакансии может только работодатель"))
        }
        if (draft.title.isBlank() || draft.companyName.isBlank() || draft.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Заполните обязательные поля вакансии"))
        }
        val vacancy = Vacancy(
            id = draft.id ?: UUID.randomUUID().toString(),
            employerId = userSession.id,
            title = draft.title,
            companyName = draft.companyName,
            city = draft.city.ifBlank { "Не указан" },
            salary = draft.salary.ifBlank { "По договоренности" },
            employmentType = draft.employmentType.ifBlank { "Полный день" },
            description = draft.description
        )
        val index = vacancies.indexOfFirst { it.id == vacancy.id }
        if (index >= 0) {
            vacancies[index] = vacancy
        } else {
            vacancies.add(0, vacancy)
        }
        return Result.success(Unit)
    }

    override suspend fun deleteVacancy(vacancyId: String, userSession: UserSession): Result<Unit> {
        if (userSession.role != UserRole.EMPLOYER) {
            return Result.failure(IllegalStateException("Удалять вакансии может только работодатель"))
        }
        val vacancy = vacancies.firstOrNull { it.id == vacancyId }
            ?: return Result.failure(NoSuchElementException("Вакансия не найдена"))
        if (vacancy.employerId != userSession.id) {
            return Result.failure(IllegalStateException("Можно удалить только свою вакансию"))
        }
        vacancies.removeAll { it.id == vacancyId }
        applications.removeAll { it.vacancyId == vacancyId }
        favorites.values.forEach { it.remove(vacancyId) }
        return Result.success(Unit)
    }
}
