package com.brelio.domain.usecase.auth

import com.brelio.domain.model.Session
import com.brelio.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: SignInUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = SignInUseCase(authRepository)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank email is rejected`() = runTest {
        useCase("   ", "password123")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `password shorter than 6 chars is rejected`() = runTest {
        useCase("user@test.com", "abc")
    }

    @Test
    fun `valid credentials are forwarded to repository`() = runTest {
        val session = Session("at", "rt", "uid-1", "user@test.com")
        coEvery { authRepository.signIn("user@test.com", "password123") } returns Result.success(session)

        val result = useCase("user@test.com", "password123")

        assertTrue(result.isSuccess)
        assertEquals(session, result.getOrNull())
        coVerify(exactly = 1) { authRepository.signIn("user@test.com", "password123") }
    }

    @Test
    fun `email is trimmed before calling repository`() = runTest {
        val session = Session("at", "rt", "uid-1", "user@test.com")
        coEvery { authRepository.signIn("user@test.com", "secret99") } returns Result.success(session)

        useCase("  user@test.com  ", "secret99")

        coVerify { authRepository.signIn("user@test.com", "secret99") }
    }

    @Test
    fun `repository failure propagates to caller`() = runTest {
        val error = RuntimeException("Network error")
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(error)

        val result = useCase("user@test.com", "password123")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
