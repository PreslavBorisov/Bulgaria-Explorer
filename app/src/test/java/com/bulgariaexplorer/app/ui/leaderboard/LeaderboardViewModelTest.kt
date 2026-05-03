package com.bulgariaexplorer.app.ui.leaderboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.LeaderboardResponse
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
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
class LeaderboardViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: LeaderboardRepository

    private val fakeEntries = (1..10).map { i ->
        LeaderboardResponse(
            rank = i,
            userId = i.toLong(),
            username = "user$i",
            totalPoints = 1000 - i * 50,
            level = 10 - i,
            streakDays = i
        )
    }

    private val fakeMyRank = LeaderboardResponse(
        rank = 42,
        userId = 99L,
        username = "me",
        totalPoints = 250,
        level = 3,
        streakDays = 5
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads leaderboard successfully`() {
        coEvery { repository.getTop10() } returns Result.success(fakeEntries)
        coEvery { repository.getMe() } returns Result.success(fakeMyRank)

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.leaderboardState.value
        assertTrue(state is LeaderboardViewModel.LeaderboardState.Success)
        assertEquals(10, (state as LeaderboardViewModel.LeaderboardState.Success).entries.size)
        assertEquals(1, state.entries.first().rank)
    }

    @Test
    fun `init loads my rank successfully`() {
        coEvery { repository.getTop10() } returns Result.success(fakeEntries)
        coEvery { repository.getMe() } returns Result.success(fakeMyRank)

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.myRankState.value
        assertTrue(state is LeaderboardViewModel.MyRankState.Success)
        assertEquals(42, (state as LeaderboardViewModel.MyRankState.Success).entry.rank)
        assertEquals("me", state.entry.username)
    }

    @Test
    fun `leaderboard error state`() {
        coEvery { repository.getTop10() } returns Result.failure(Exception("Server down"))
        coEvery { repository.getMe() } returns Result.success(fakeMyRank)

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.leaderboardState.value
        assertTrue(state is LeaderboardViewModel.LeaderboardState.Error)
        assertEquals("Server down", (state as LeaderboardViewModel.LeaderboardState.Error).message)
    }

    @Test
    fun `my rank error state`() {
        coEvery { repository.getTop10() } returns Result.success(fakeEntries)
        coEvery { repository.getMe() } returns Result.failure(Exception("Not found"))

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.myRankState.value
        assertTrue(state is LeaderboardViewModel.MyRankState.Error)
        assertEquals("Not found", (state as LeaderboardViewModel.MyRankState.Error).message)
    }

    @Test
    fun `leaderboard error with null message uses default`() {
        coEvery { repository.getTop10() } returns Result.failure(Exception())
        coEvery { repository.getMe() } returns Result.success(fakeMyRank)

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.leaderboardState.value
        assertTrue(state is LeaderboardViewModel.LeaderboardState.Error)
        assertEquals("Failed to load leaderboard", (state as LeaderboardViewModel.LeaderboardState.Error).message)
    }

    @Test
    fun `my rank error with null message uses default`() {
        coEvery { repository.getTop10() } returns Result.success(fakeEntries)
        coEvery { repository.getMe() } returns Result.failure(Exception())

        val viewModel = LeaderboardViewModel(repository)

        val state = viewModel.myRankState.value
        assertTrue(state is LeaderboardViewModel.MyRankState.Error)
        assertEquals("Failed to load rank", (state as LeaderboardViewModel.MyRankState.Error).message)
    }
}
