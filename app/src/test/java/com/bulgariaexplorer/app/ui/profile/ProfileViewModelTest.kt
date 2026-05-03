package com.bulgariaexplorer.app.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.AchievementResponse
import com.bulgariaexplorer.app.data.remote.dto.UserResponse
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
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
class ProfileViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var leaderboardRepository: LeaderboardRepository

    private val fakeUser = UserResponse(
        id = 1L,
        username = "explorer",
        email = "explorer@example.com",
        totalPoints = 750,
        level = 5,
        streakDays = 12,
        role = "USER"
    )

    private val fakeAchievements = listOf(
        AchievementResponse(
            id = 1L,
            code = "FIRST_VISIT",
            title = "First Visit",
            description = "Complete your first visit",
            iconName = "star",
            unlockedAt = "2024-01-15T10:30:00"
        ),
        AchievementResponse(
            id = 2L,
            code = "TEN_VISITS",
            title = "Explorer",
            description = "Complete 10 visits",
            iconName = "explore",
            unlockedAt = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        leaderboardRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads profile successfully`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.success(fakeAchievements)

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.userState.value
        assertTrue(state is ProfileViewModel.UserState.Success)
        assertEquals(fakeUser, (state as ProfileViewModel.UserState.Success).user)
        assertEquals(750, state.user.totalPoints)
    }

    @Test
    fun `init loads achievements successfully`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.success(fakeAchievements)

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.achievementsState.value
        assertTrue(state is ProfileViewModel.AchievementsState.Success)
        assertEquals(2, (state as ProfileViewModel.AchievementsState.Success).achievements.size)
        assertEquals("First Visit", state.achievements[0].title)
    }

    @Test
    fun `profile error state`() {
        coEvery { userRepository.getCurrentUser() } returns Result.failure(Exception("Session expired"))
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.success(fakeAchievements)

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.userState.value
        assertTrue(state is ProfileViewModel.UserState.Error)
        assertEquals("Session expired", (state as ProfileViewModel.UserState.Error).message)
    }

    @Test
    fun `achievements error state`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.failure(Exception("Timeout"))

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.achievementsState.value
        assertTrue(state is ProfileViewModel.AchievementsState.Error)
        assertEquals("Timeout", (state as ProfileViewModel.AchievementsState.Error).message)
    }

    @Test
    fun `profile error with null message uses default`() {
        coEvery { userRepository.getCurrentUser() } returns Result.failure(Exception())
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.success(emptyList())

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.userState.value
        assertTrue(state is ProfileViewModel.UserState.Error)
        assertEquals("Failed to load profile", (state as ProfileViewModel.UserState.Error).message)
    }

    @Test
    fun `achievements error with null message uses default`() {
        coEvery { userRepository.getCurrentUser() } returns Result.success(fakeUser)
        coEvery { leaderboardRepository.getMyAchievements() } returns Result.failure(Exception())

        val viewModel = ProfileViewModel(userRepository, leaderboardRepository)

        val state = viewModel.achievementsState.value
        assertTrue(state is ProfileViewModel.AchievementsState.Error)
        assertEquals("Failed to load achievements", (state as ProfileViewModel.AchievementsState.Error).message)
    }
}
