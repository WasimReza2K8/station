package com.example.featurestation.ui.map.viewmodel

import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output
import com.example.domain.domain.model.StationInfo
import com.example.domain.domain.usecase.GetStationsUseCase
import com.example.featurestation.model.StationClusterItem
import com.example.featurestation.model.StationUiInfo
import com.example.featurestation.ui.map.viewmodel.StationContract.Effect.NetworkErrorEffect
import com.example.featurestation.ui.map.viewmodel.StationContract.Effect.UnknownErrorEffect
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnMapClicked
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnMarkerClicked
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnViewStarted
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnViewStopped
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class TrackViewModelTest {
    private lateinit var useCase: GetStationsUseCase
    private lateinit var viewModel: StationViewModel
    private val marker: Marker = mockk()
    private val resourceProvider: ResourceProvider = mockk {
        every {
            getString(any(), any())
        } returns "mock"
    }

    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        useCase = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = initViewModel()
    }

    private fun initViewModel() = StationViewModel(
        getStationsUseCase = useCase,
        resourceProvider = resourceProvider,
    )

    private fun domainList(): List<StationInfo> {
        val stationInfo = StationInfo(
            id = 1,
            title = "test title",
            addressLine1 = "address1",
            addressLine2 = "address2",
            postCode = "postCode",
            town = "town",
            lat = 53.02,
            lng = 13.32,
            numberOfPoints = 2,
        )
        return listOf(
            stationInfo,
            stationInfo.copy(id = 2, lat = 100.5, lng = 32.6),
            stationInfo.copy(id = 3, lat = 10.5, lng = 54.6),
        )
    }

    private fun uiList(): List<StationUiInfo> {
        val stationUiInfo = StationUiInfo(
            id = 1,
            title = "test title",
            clusterItem = StationClusterItem(53.02, 13.32, "test title", ""),
            address = "address1\naddress2\npostCode, town",
            numberOfPoints = "mock",
        )
        return listOf(
            stationUiInfo,
            stationUiInfo.copy(
                id = 2,
                clusterItem = StationClusterItem(100.5, 32.6, "test title", ""),
            ),
            stationUiInfo.copy(
                id = 3,
                clusterItem = StationClusterItem(10.5, 54.6, "test title", ""),

                )
        )
    }

    @Test
    fun `When onViewStarted, Then provide success state`() = runTest {
        val provided = Output.Success(domainList())
        val expected = uiList()
        coEvery {
            useCase(any())
        } returns flow { emit(provided) }

        viewModel.onEvent(OnViewStarted)

        viewModel.viewState
            .take(1)
            .collectLatest { state ->
                assertThat(state.stationUiInfoList == expected).isTrue
            }
    }

    @Test
    fun `When onViewStarted, Then provide networkError effect`() = runTest {
        coEvery {
            useCase(any())
        } returns flow { emit(Output.NetworkError) }

        viewModel.onEvent(OnViewStarted)

        viewModel.effect
            .take(1)
            .collectLatest { effect ->
                assertThat(effect is NetworkErrorEffect).isTrue
            }
    }

    @Test
    fun `When onViewStarted, Then provide unknownError effect`() = runTest {
        coEvery {
            useCase(any())
        } returns flow { emit(Output.UnknownError) }

        viewModel.onEvent(OnViewStarted)

        viewModel.effect
            .take(1)
            .collectLatest { effect ->
                assertThat(effect is UnknownErrorEffect).isTrue
            }
    }

    @Test
    fun `When onRetry clicked, Then provide success state`() = runTest {
        val provided = Output.Success(domainList())
        val expected = uiList()
        coEvery {
            useCase(any())
        } returns flow { emit(provided) }

        viewModel.onEvent(StationContract.Event.OnRetry)

        viewModel.viewState
            .take(1)
            .collectLatest { state ->
                assertThat(state.stationUiInfoList == expected).isTrue
            }
    }

    @Test
    fun `When onRetry clicked, Then provide networkError effect`() = runTest {
        coEvery {
            useCase(any())
        } returns flow { emit(Output.NetworkError) }

        viewModel.onEvent(StationContract.Event.OnRetry)

        viewModel.effect
            .take(1)
            .collectLatest { effect ->
                assertThat(effect is NetworkErrorEffect).isTrue
            }
    }

    @Test
    fun `When onRetry clicked, Then provide unknownError effect`() = runTest {
        coEvery {
            useCase(any())
        } returns flow { emit(Output.UnknownError) }

        viewModel.onEvent(StationContract.Event.OnRetry)

        viewModel.effect
            .take(1)
            .collectLatest { effect ->
                assertThat(effect is UnknownErrorEffect).isTrue
            }
    }

    @Test
    fun `When OnMarkerClicked, Then change selected Station state`() = runTest {
        val provided = Output.Success(domainList())
        val expected = uiList()[0]
        coEvery {
            useCase(any())
        } returns flow { emit(provided) }

        every {
            marker.position
        } returns LatLng(53.02, 13.32)

        viewModel.onEvent(OnViewStarted)

        viewModel.onEvent(OnMarkerClicked(marker))

        viewModel.viewState
            .take(1)
            .collectLatest { state ->
                assertThat(state.selectedStation == expected).isTrue
            }
    }

    @Test
    fun `When OnMapClicked, Then change selected Station become null`() = runTest {
        val provided = Output.Success(domainList())
        val expected = null
        coEvery {
            useCase(any())
        } returns flow { emit(provided) }

        every {
            marker.position
        } returns LatLng(53.02, 13.32)

        viewModel.onEvent(OnViewStarted)

        viewModel.onEvent(OnMapClicked)

        viewModel.viewState
            .take(1)
            .collectLatest { state ->
                assertThat(state.selectedStation == expected).isTrue
            }
    }

    @Test
    fun `When OnViewStopped, Then the coroutine is cancelled`() = runTest {
        val provided = Output.Success(domainList())
        coEvery {
            useCase(any())
        } returns flow { emit(provided) }

        every {
            marker.position
        } returns LatLng(53.02, 13.32)

        viewModel.onEvent(OnViewStarted)

        viewModel.onEvent(OnViewStopped)

        assertThat(!viewModel.job!!.isActive).isTrue
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
