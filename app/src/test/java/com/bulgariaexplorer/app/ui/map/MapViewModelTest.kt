package com.bulgariaexplorer.app.ui.map

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
class MapViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var poiRepository: PoiRepository

    private fun makePoi(id: Long) = PoiResponse(
        id = id,
        title = "POI $id",
        shortDescription = "Short $id",
        fullDescription = "Full $id",
        latitude = 42.0 + id * 0.1,
        longitude = 25.0 + id * 0.1,
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
        poiRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads POIs successfully`() {
        val pois = listOf(makePoi(1), makePoi(2), makePoi(3))
        coEvery { poiRepository.getAllPois() } returns Result.success(pois)

        val viewModel = MapViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is MapViewModel.PoiState.Success)
        assertEquals(3, (state as MapViewModel.PoiState.Success).pois.size)
    }

    @Test
    fun `init loads empty POIs list`() {
        coEvery { poiRepository.getAllPois() } returns Result.success(emptyList())

        val viewModel = MapViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is MapViewModel.PoiState.Success)
        assertTrue((state as MapViewModel.PoiState.Success).pois.isEmpty())
    }

    @Test
    fun `init loads POIs error`() {
        coEvery { poiRepository.getAllPois() } returns Result.failure(Exception("Connection refused"))

        val viewModel = MapViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is MapViewModel.PoiState.Error)
        assertEquals("Connection refused", (state as MapViewModel.PoiState.Error).message)
    }

    @Test
    fun `error with null message uses default`() {
        coEvery { poiRepository.getAllPois() } returns Result.failure(Exception())

        val viewModel = MapViewModel(poiRepository)

        val state = viewModel.poisState.value
        assertTrue(state is MapViewModel.PoiState.Error)
        assertEquals("Failed to load POIs", (state as MapViewModel.PoiState.Error).message)
    }
}
