package com.bulgariaexplorer.app.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.auth.AuthResponse
import com.bulgariaexplorer.app.data.remote.dto.auth.UserDto
import com.bulgariaexplorer.app.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    private val fakeUser = UserDto(
        id = 1L,
        username = "testuser",
        email = "test@example.com",
        points = 100,
        level = 2,
        currentStreak = 5,
        role = "USER"
    )

    private val fakeAuthResponse = AuthResponse(
        token = "fake-jwt-token",
        user = fakeUser
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success sets Success state`() {
        coEvery { authRepository.login("test@example.com", "password") } returns Result.success(fakeAuthResponse)

        viewModel = AuthViewModel(authRepository)
        viewModel.login("test@example.com", "password")

        val state = viewModel.loginState.value
        assertTrue(state is AuthViewModel.AuthState.Success)
        assertEquals(fakeAuthResponse, (state as AuthViewModel.AuthState.Success).authResponse)
    }

    @Test
    fun `login failure sets Error state`() {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Invalid credentials"))

        viewModel = AuthViewModel(authRepository)
        viewModel.login("bad@example.com", "wrong")

        val state = viewModel.loginState.value
        assertTrue(state is AuthViewModel.AuthState.Error)
        assertEquals("Invalid credentials", (state as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `login failure with null message uses default`() {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception())

        viewModel = AuthViewModel(authRepository)
        viewModel.login("test@example.com", "password")

        val state = viewModel.loginState.value
        assertTrue(state is AuthViewModel.AuthState.Error)
        assertEquals("Login failed", (state as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `register success sets Success state`() {
        coEvery { authRepository.register("testuser", "test@example.com", "password") } returns Result.success(fakeAuthResponse)

        viewModel = AuthViewModel(authRepository)
        viewModel.register("testuser", "test@example.com", "password")

        val state = viewModel.registerState.value
        assertTrue(state is AuthViewModel.AuthState.Success)
        assertEquals(fakeAuthResponse, (state as AuthViewModel.AuthState.Success).authResponse)
    }

    @Test
    fun `register failure sets Error state`() {
        coEvery { authRepository.register(any(), any(), any()) } returns Result.failure(Exception("Email already exists"))

        viewModel = AuthViewModel(authRepository)
        viewModel.register("testuser", "test@example.com", "password")

        val state = viewModel.registerState.value
        assertTrue(state is AuthViewModel.AuthState.Error)
        assertEquals("Email already exists", (state as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `register failure with null message uses default`() {
        coEvery { authRepository.register(any(), any(), any()) } returns Result.failure(Exception())

        viewModel = AuthViewModel(authRepository)
        viewModel.register("testuser", "test@example.com", "password")

        val state = viewModel.registerState.value
        assertTrue(state is AuthViewModel.AuthState.Error)
        assertEquals("Registration failed", (state as AuthViewModel.AuthState.Error).message)
    }
}
