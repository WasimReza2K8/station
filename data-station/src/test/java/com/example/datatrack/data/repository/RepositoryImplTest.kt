package com.example.datatrack.data.repository

import app.cash.turbine.test
import com.example.datatrack.data.datasource.RemoteDataSource
import com.example.datatrack.data.dto.StationDto
import com.example.datatrack.data.utils.TestData
import com.example.domain.domain.model.StationInfo
import com.example.domain.domain.usecase.GetStationsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.IOException

class RepositoryImplTest {
    private val remoteSource: RemoteDataSource = mockk()
    private val repository = RepositoryImpl(remoteSource)

    @Test
    fun `Given valid remoteSource response, When getStations called with input, Then emit valid stations`() = runTest {
        val provided = TestData.stationDto
        val expected = listOf(TestData.stationInfo)
        coEvery {
            remoteSource.getRemoteStations(any())
        } returns flow { emit(listOf(provided)) }

        repository.getStations(
            GetStationsUseCase.Input(
                lat = 53.45,
                lng = 13.46,
                distance = 5,
                distanceUnit = "km",
            )
        ).test {
            assertThat(expected == awaitItem()).isTrue
            awaitComplete()
        }
    }

    @Test
    fun `Given empty remoteSource response, When getStations called with input, Then emit empty stations`() = runTest {
        val provided = emptyList<StationDto>()
        val expected = emptyList<StationInfo>()
        coEvery {
            remoteSource.getRemoteStations(any())
        } returns flow { emit(provided) }

        repository.getStations(
            GetStationsUseCase.Input(
                lat = 53.45,
                lng = 13.46,
                distance = 5,
                distanceUnit = "km",
            )
        ).test {
            assertThat(expected == awaitItem()).isTrue
            awaitComplete()
        }
    }

    @Test
    fun `Given IoException remoteSource response, When getStations called, Then emit exception`() =
        runTest {
            val providedData = IOException()
            coEvery {
                remoteSource.getRemoteStations(any())
            } returns flow { throw providedData }

            repository.getStations(
                GetStationsUseCase.Input(
                    lat = 53.45,
                    lng = 13.46,
                    distance = 5,
                    distanceUnit = "km",
                )
            ).test {
                assertThat(providedData == awaitError()).isTrue
            }
        }
}
