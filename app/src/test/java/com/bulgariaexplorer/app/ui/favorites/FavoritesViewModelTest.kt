package com.bulgariaexplorer.app.ui.favorites

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.FavoriteResponse
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class FavoritesViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var favoriteRepository: FavoriteRepository

    private val fakeFavorites = listOf(
        FavoriteResponse(
            id = 1L,
            poiId = 10L,
            poiTitle = "Rila Monastery",
            categoryName = "Culture",
            city = "Rila",
            region = "Kyustendil",
            imageUrl = null
        ),
        FavoriteResponse(
            id = 2L,
            poiId = 20L,
            poiTitle = "Seven Rila Lakes",
            categoryName = "Nature",
            city = "Rila",
            region = "Kyustendil",
            imageUrl = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        favoriteRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `init loads favorites successfully`() {
        coEvery { favoriteRepository.getFavorites() } returns Result.success(fakeFavorites)

        val viewModel = FavoritesViewModel(favoriteRepository)

        val state = viewModel.favoritesState.value
        assertTrue(state is FavoritesViewModel.FavoritesState.Success)
        assertEquals(2, (state as FavoritesViewModel.FavoritesState.Success).favorites.size)
        assertEquals("Rila Monastery", state.favorites[0].poiTitle)
    }

    @Test
    fun `init loads empty favorites list`() {
        coEvery { favoriteRepository.getFavorites() } returns Result.success(emptyList())

        val viewModel = FavoritesViewModel(favoriteRepository)

        val state = viewModel.favoritesState.value
        assertTrue(state is FavoritesViewModel.FavoritesState.Success)
        assertTrue((state as FavoritesViewModel.FavoritesState.Success).favorites.isEmpty())
    }

    @Test
    fun `init loads favorites error`() {
        coEvery { favoriteRepository.getFavorites() } returns Result.failure(Exception("Unauthorized"))

        val viewModel = FavoritesViewModel(favoriteRepository)

        val state = viewModel.favoritesState.value
        assertTrue(state is FavoritesViewModel.FavoritesState.Error)
        assertEquals("Unauthorized", (state as FavoritesViewModel.FavoritesState.Error).message)
    }

    @Test
    fun `error with null message uses default`() {
        coEvery { favoriteRepository.getFavorites() } returns Result.failure(Exception())

        val viewModel = FavoritesViewModel(favoriteRepository)

        val state = viewModel.favoritesState.value
        assertTrue(state is FavoritesViewModel.FavoritesState.Error)
        assertEquals("Failed to load favorites", (state as FavoritesViewModel.FavoritesState.Error).message)
    }
}
