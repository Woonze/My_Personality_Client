package com.example.mypersonality.di

import com.example.mypersonality.data.auth.AuthRepository
import com.example.mypersonality.data.auth.InMemoryAuthRepository
import com.example.mypersonality.data.auth.ServerAuthRepository
import com.example.mypersonality.data.vacancy.InMemoryVacancyRepository
import com.example.mypersonality.data.vacancy.RetrofitVacancyRepository
import com.example.mypersonality.data.vacancy.VacancyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: ServerAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVacancyRepository(impl: RetrofitVacancyRepository): VacancyRepository
}
