package com.bulgariaexplorer.app.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.remote.dto.UserResponse
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.UserRepository
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
class HomeViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var poiRepository: PoiRepository

    private val fakeUser = UserResponse(
        id = 1L,
        username = "testuser",
        email = "test@example.com",
        totalPoints = 500,
        level = 3,
        streakDays = 7,
        role = "USER"
    )

    private fun makePoi(id: Long) = PoiResponse(
        id = id,
        title = "POI $id",
        shortDescription = "Short $id",
        fullDescription = "Full $id",
        latitude = 42.0 + id,
        longitude = 25.0 + id,
        rewardPoints = 10,
        imageUrl = null,
        imageUrls = null,
        visitPhotoUrls = null,
        categoryName = "Nature",
        categoryCode = "NATURE",
        address = "Address $id",
        city = "Sofia",
        region = "Sofia",
        active = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        poiRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads user data successfully`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { poiRepository.getAllPois() } returns Result.success(emptyList())

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.userState.value
        assertTrue(state is HomeViewModel.UserState.Success)
        assertEquals(fakeUser, (state as HomeViewModel.UserState.Success).user)
    }

    @Test
    fun `init loads user data error`() {
        coEvery { userRepository.getCurrentUser() } returns Result.failure(Exception("Network error"))
        coEvery { poiRepository.getAllPois() } returns Result.success(emptyList())

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.userState.value
        assertTrue(state is HomeViewModel.UserState.Error)
        assertEquals("Network error", (state as HomeViewModel.UserState.Error).message)
    }

    @Test
    fun `init loads featured POIs limited to 5`() {
        val pois = (1L..10L).map { makePoi(it) }
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.featuredPoisState.value
        assertTrue(state is HomeViewModel.PoiState.Success)
        assertEquals(5, (state as HomeViewModel.PoiState.Success).pois.size)
        assertEquals(1L, state.pois.first().id)
        assertEquals(5L, state.pois.last().id)
    }

    @Test
    fun `featured POIs with fewer than 5 returns all`() {
        val pois = (1L..3L).map { makePoi(it) }
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.featuredPoisState.value
        assertTrue(state is HomeViewModel.PoiState.Success)
        assertEquals(3, (state as HomeViewModel.PoiState.Success).pois.size)
    }

    @Test
    fun `featured POIs error state`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { poiRepository.getAllPois() } returns Result.failure(Exception("Server error"))

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.featuredPoisState.value
        assertTrue(state is HomeViewModel.PoiState.Error)
        assertEquals("Server error", (state as HomeViewModel.PoiState.Error).message)
    }

    @Test
    fun `refresh reloads both user and POIs`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { poiRepository.getAllPois() } returns Result.success(listOf(makePoi(1)))

        val viewModel = HomeViewModel(userRepository, poiRepository)

        // Change stub to return different data
        val updatedUser = fakeUser.copy(totalPoints = 999)
        coEvery { userRepository.getCurrentUser() } returns Result.success(updatedUser)

        viewModel.refresh()

        val userState = viewModel.userState.value as HomeViewModel.UserState.Success
        assertEquals(999, userState.user.totalPoints)
    }

    @Test
    fun `user error with null message uses default`() {
        coEvery { userRepository.getCurrentUser() } returns Result.failure(Exception())
        coEvery { poiRepository.getAllPois() } returns Result.success(emptyList())

        val viewModel = HomeViewModel(userRepository, poiRepository)

        val state = viewModel.userState.value
        assertTrue(state is HomeViewModel.UserState.Error)
        assertEquals("Failed to load user", (state as HomeViewModel.UserState.Error).message)
    }
}
