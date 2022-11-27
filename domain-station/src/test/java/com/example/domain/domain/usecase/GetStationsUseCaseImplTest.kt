package com.example.domain.domain.usecase

import app.cash.turbine.test
import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.state.Output
import com.example.domain.domain.repository.Repository
import com.example.domain.utils.TestData
import com.example.domain.utils.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.IOException

class GetStationsUseCaseImplTest {
    private val repository: Repository = mockk()
    private val testDispatcher: BaseDispatcherProvider = TestDispatcherProvider()
    private val useCase = GetStationsUseCaseImpl(repository, testDispatcher)

    @Test
    fun `Given valid response, When GetStationsUseCase invoke, Then emit success output`() = runTest {
        val provided = TestData.stations
        val expected = Output.Success(provided)

        coEvery {
            repository.getStations(any(), any(), any(), any())
        } returns flow { emit(provided) }

        useCase(
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
    fun `Given IoException, When GetStationsUseCase invoke, Then emit networkError output`() = runTest {
        val provided = IOException("test")
        val expected = Output.NetworkError
        coEvery {
            repository.getStations(any(), any(), any(), any())
        } returns flow { throw provided }

        useCase(
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
    fun `Given IllegalStateException response, When GetStationsUseCase invoke, Then emit unknownError output`() =
        runTest {
            val provided = IllegalStateException("test")
            val expected = Output.UnknownError
            coEvery {
                repository.getStations(any(), any(), any(), any())
            } returns flow { throw provided }

            useCase(
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
}
