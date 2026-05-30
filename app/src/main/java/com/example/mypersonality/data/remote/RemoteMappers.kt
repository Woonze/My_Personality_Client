package com.example.mypersonality.data.remote

import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.core.model.Vacancy
import com.example.mypersonality.core.model.VacancyApplication
import com.example.mypersonality.core.model.VacancyDraft

fun ProfileDto.toUserSession(): UserSession = UserSession(
    id = id,
    email = email,
    fullName = fullName,
    role = role
)

fun VacancyDto.toModel(isFavorite: Boolean = false): Vacancy = Vacancy(
    id = id,
    employerId = employerId,
    companyName = companyName,
    title = title,
    city = city,
    salary = salary,
    employmentType = employmentType,
    description = description,
    isFavorite = isFavorite
)

fun VacancyDraft.toRequestDto(): VacancyRequestDto = VacancyRequestDto(
    id = id,
    companyName = companyName,
    title = title,
    city = city,
    salary = salary,
    employmentType = employmentType,
    description = description
)

fun ApplicationDto.toModel(): VacancyApplication = VacancyApplication(
    id = id,
    vacancyId = vacancyId,
    vacancyTitle = vacancyTitle,
    applicantId = applicantId,
    applicantName = applicantName,
    employerId = employerId,
    coverLetter = coverLetter,
    status = status
)
