package com.example.mypersonality.core.model

import kotlinx.serialization.Serializable

enum class UserRole {
    SEEKER,
    EMPLOYER
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun isDark(): Boolean = this == DARK
}

@Serializable
data class UserSession(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole
)

@Serializable
data class Vacancy(
    val id: String,
    val employerId: String,
    val companyName: String,
    val title: String,
    val city: String,
    val salary: String,
    val employmentType: String,
    val description: String,
    val isFavorite: Boolean = false
)

data class VacancyDraft(
    val id: String? = null,
    val title: String = "",
    val companyName: String = "",
    val city: String = "",
    val salary: String = "",
    val employmentType: String = "",
    val description: String = ""
)

data class VacancyApplication(
    val id: String,
    val vacancyId: String,
    val vacancyTitle: String,
    val applicantId: String,
    val applicantName: String,
    val employerId: String,
    val coverLetter: String,
    val status: String
)
