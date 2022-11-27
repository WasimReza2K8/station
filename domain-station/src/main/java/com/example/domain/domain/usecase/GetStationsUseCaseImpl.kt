package com.example.domain.domain.usecase

import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.ext.isNetworkException
import com.example.core.state.Output
import com.example.domain.domain.model.StationInfo
import com.example.domain.domain.repository.Repository
import com.example.domain.domain.usecase.GetStationsUseCase.Input
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStationsUseCaseImpl @Inject constructor(
    private val repository: Repository,
    private val mainDispatcherProvider: BaseDispatcherProvider
) : GetStationsUseCase {
    override fun invoke(input: Input): Flow<Output<List<StationInfo>>> {
        return repository.getStations(
            lat = input.lat,
            lng = input.lng,
            distance = input.distance,
            distanceUnit = input.distanceUnit,
        ).map { stations ->
            getStationsOutput(stations)
        }.catch { exception ->
            if (exception.isNetworkException()) {
                emit(Output.NetworkError)
            } else {
                emit(Output.UnknownError)
            }
        }.flowOn(mainDispatcherProvider.io())
    }

    private fun getStationsOutput(stations: List<StationInfo>): Output<List<StationInfo>> =
        Output.Success(stations)
}
