package com.bulgariaexplorer.app.ui.poi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.repository.PoiRepository
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
class PoiViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var poiRepository: PoiRepository

    private fun makePoi(id: Long, title: String = "POI $id", city: String = "Sofia") = PoiResponse(
        id = id,
        title = title,
        shortDescription = "Short $id",
        fullDescription = "Full $id",
        latitude = 42.0,
        longitude = 25.0,
        rewardPoints = 10,
        imageUrl = null,
        imageUrls = null,
        visitPhotoUrls = null,
        categoryName = "Nature",
        categoryCode = "NATURE",
        address = "Address $id",
        city = city,
        region = "Region",
        active = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        poiRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads POIs successfully`() {
        val pois = listOf(makePoi(1), makePoi(2))
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = PoiViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is PoiViewModel.PoiState.Success)
        assertEquals(2, (state as PoiViewModel.PoiState.Success).pois.size)
    }

    @Test
    fun `init loads POIs error`() {
        coEvery { poiRepository.getAllPois() } returns Result.failure(Exception("Network error"))

        val viewModel = PoiViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is PoiViewModel.PoiState.Error)
        assertEquals("Network error", (state as PoiViewModel.PoiState.Error).message)
    }

    @Test
    fun `searchPois filters by title`() {
        val pois = listOf(
            makePoi(1, title = "Rila Monastery"),
            makePoi(2, title = "Plovdiv Old Town"),
            makePoi(3, title = "Rila Lakes")
        )
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = PoiViewModel(poiRepository)
        viewModel.searchPois("Rila")

        val state = viewModel.poisState.value as PoiViewModel.PoiState.Success
        assertEquals(2, state.pois.size)
        assertTrue(state.pois.all { it.title!!.contains("Rila") })
    }

    @Test
    fun `searchPois filters by city`() {
        val pois = listOf(
            makePoi(1, city = "Sofia"),
            makePoi(2, city = "Plovdiv"),
            makePoi(3, city = "Sofia")
        )
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = PoiViewModel(poiRepository)
        viewModel.searchPois("Sofia")

        val state = viewModel.poisState.value as PoiViewModel.PoiState.Success
        assertEquals(2, state.pois.size)
    }

    @Test
    fun `empty search resets to all POIs`() {
        val pois = listOf(makePoi(1), makePoi(2), makePoi(3))
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = PoiViewModel(poiRepository)
        viewModel.searchPois("POI 1")

        val filtered = viewModel.poisState.value as PoiViewModel.PoiState.Success
        assertEquals(1, filtered.pois.size)

        viewModel.searchPois("")

        val reset = viewModel.poisState.value as PoiViewModel.PoiState.Success
        assertEquals(3, reset.pois.size)
    }

    @Test
    fun `searchPois with no matches returns empty list`() {
        val pois = listOf(makePoi(1, title = "Rila"), makePoi(2, title = "Plovdiv"))
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = PoiViewModel(poiRepository)
        viewModel.searchPois("Nonexistent")

        val state = viewModel.poisState.value as PoiViewModel.PoiState.Success
        assertTrue(state.pois.isEmpty())
    }

    @Test
    fun `error with null message uses default`() {
        coEvery { poiRepository.getAllPois() } returns Result.failure(Exception())

        val viewModel = PoiViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is PoiViewModel.PoiState.Error)
        assertEquals("Failed to load POIs", (state as PoiViewModel.PoiState.Error).message)
    }
}
