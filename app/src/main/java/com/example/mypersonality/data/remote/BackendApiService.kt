package com.example.mypersonality.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): ProfileDto

    @GET("vacancies")
    suspend fun searchVacancies(@Query("query") query: String): List<VacancyDto>

    @GET("vacancies/favorites/{userId}")
    suspend fun getFavorites(@Path("userId") userId: String): List<VacancyDto>

    @GET("vacancies/employer/{userId}")
    suspend fun getEmployerVacancies(@Path("userId") userId: String): List<VacancyDto>

    @GET("vacancies/{vacancyId}")
    suspend fun getVacancy(@Path("vacancyId") vacancyId: String): VacancyDto

    @POST("vacancies")
    suspend fun createVacancy(
        @Query("employerId") employerId: String,
        @Body request: VacancyRequestDto
    ): VacancyDto

    @PUT("vacancies/{vacancyId}")
    suspend fun updateVacancy(
        @Path("vacancyId") vacancyId: String,
        @Query("employerId") employerId: String,
        @Body request: VacancyRequestDto
    ): VacancyDto

    @DELETE("vacancies/{vacancyId}")
    suspend fun deleteVacancy(
        @Path("vacancyId") vacancyId: String,
        @Query("employerId") employerId: String
    )

    @POST("vacancies/{vacancyId}/favorite/{userId}")
    suspend fun toggleFavorite(
        @Path("vacancyId") vacancyId: String,
        @Path("userId") userId: String
    ): Map<String, Boolean>

    @POST("vacancies/{vacancyId}/apply/{userId}")
    suspend fun applyToVacancy(
        @Path("vacancyId") vacancyId: String,
        @Path("userId") userId: String,
        @Body request: ApplicationRequestDto
    ): ApplicationDto

    @GET("vacancies/applications/seeker/{userId}")
    suspend fun getSeekerApplications(@Path("userId") userId: String): List<ApplicationDto>

    @GET("vacancies/{vacancyId}/applications")
    suspend fun getVacancyApplications(@Path("vacancyId") vacancyId: String): List<ApplicationDto>
}
