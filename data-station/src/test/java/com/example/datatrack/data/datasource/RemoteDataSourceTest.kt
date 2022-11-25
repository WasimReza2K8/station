package com.example.datatrack.data.datasource

import app.cash.turbine.test
import com.example.datatrack.data.api.Api
import com.example.datatrack.data.dto.StationDto
import com.example.datatrack.data.utils.TestData
import com.example.domain.domain.usecase.GetStationsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RemoteDataSourceTest {
    private val service: Api = mockk()
    private val remoteDataSource = RemoteDataSource(service)

    @Test
    fun `Given valid api response, When getRemoteStations called with valid input, Then emit valid output`() =
        runTest {
            val provided = TestData.responseDto
            coEvery {
                service.getChargingStations(any(), any(), any(), any())
            } returns provided

            remoteDataSource.getRemoteStations(
                GetStationsUseCase.Input(
                    lat = 53.45,
                    lng = 13.46,
                    distance = 5,
                    distanceUnit = "km",
                )
            ).test {
                assertThat(provided == awaitItem()).isTrue
            }
        }

    @Test
    fun `Given empty api response, When getRemoteStations called, Then emit empty output`() = runTest {
        val provided = emptyList<StationDto>()
        coEvery {
            service.getChargingStations(any(), any(), any(), any())
        } returns provided

        remoteDataSource.getRemoteStations(
            GetStationsUseCase.Input(
                lat = 53.45,
                lng = 13.46,
                distance = 5,
                distanceUnit = "km",
            )
        ).test {
            assertThat(provided == awaitItem()).isTrue
        }
    }

    @Test
    fun `Given valid api response, When getRemoteStations called with valid input after every 30s, Then emit valid output`() =
        runTest {
            val provided = TestData.responseDto
            val items = mutableListOf<List<StationDto>>()
            coEvery {
                service.getChargingStations(any(), any(), any(), any())
            } returns provided

            remoteDataSource.getRemoteStations(
                GetStationsUseCase.Input(
                    lat = 53.45,
                    lng = 13.46,
                    distance = 5,
                    distanceUnit = "km",
                )
            ).take(2).collectLatest {
                items.add(it)
            }
            advanceTimeBy(60000)
            assertThat(items.size == 2).isTrue
        }
}
