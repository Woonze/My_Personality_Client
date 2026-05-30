package com.example.mypersonality

import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.core.model.UserSession
import com.example.mypersonality.data.auth.InMemoryAuthRepository
import com.example.mypersonality.data.vacancy.InMemoryVacancyRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `auth repository logs in with demo code`() = runBlocking {
        val repository = InMemoryAuthRepository()

        val session = repository.register(
            email = "demo@example.com",
            password = "123456",
            fullName = "Иван Петров",
            role = UserRole.SEEKER
        ).getOrThrow()

        assertThat(session.email).isEqualTo("demo@example.com")
        assertThat(repository.currentSession.value?.fullName).isEqualTo("Иван Петров")
    }

    @Test
    fun `vacancy repository toggles favorites`() = runBlocking {
        val repository = InMemoryVacancyRepository()
        val session = UserSession(
            id = "seeker-1",
            email = "seeker@example.com",
            fullName = "Соискатель",
            role = UserRole.SEEKER
        )

        repository.toggleFavorite("v1", session)
        val favorites = repository.getFavoriteVacancies(session).getOrThrow()

        assertThat(favorites).hasSize(1)
        assertThat(favorites.first().id).isEqualTo("v1")
    }
}
