package com.example.mypersonality.data.remote

import com.example.mypersonality.core.model.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("role") val role: UserRole
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class AuthResponseDto(
    @SerialName("profile") val profile: ProfileDto
)

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: UserRole,
    @SerialName("city") val city: String,
    @SerialName("about") val about: String
)

@Serializable
data class VacancyDto(
    @SerialName("id") val id: String,
    @SerialName("employerId") val employerId: String,
    @SerialName("companyName") val companyName: String,
    @SerialName("title") val title: String,
    @SerialName("city") val city: String,
    @SerialName("salary") val salary: String,
    @SerialName("employmentType") val employmentType: String,
    @SerialName("description") val description: String
)

@Serializable
data class VacancyRequestDto(
    @SerialName("id") val id: String? = null,
    @SerialName("companyName") val companyName: String,
    @SerialName("title") val title: String,
    @SerialName("city") val city: String,
    @SerialName("salary") val salary: String,
    @SerialName("employmentType") val employmentType: String,
    @SerialName("description") val description: String
)

@Serializable
data class ApplicationRequestDto(
    @SerialName("vacancyId") val vacancyId: String,
    @SerialName("coverLetter") val coverLetter: String
)

@Serializable
data class ApplicationDto(
    @SerialName("id") val id: String,
    @SerialName("vacancyId") val vacancyId: String,
    @SerialName("vacancyTitle") val vacancyTitle: String,
    @SerialName("applicantId") val applicantId: String,
    @SerialName("applicantName") val applicantName: String,
    @SerialName("employerId") val employerId: String,
    @SerialName("coverLetter") val coverLetter: String,
    @SerialName("status") val status: String
)
