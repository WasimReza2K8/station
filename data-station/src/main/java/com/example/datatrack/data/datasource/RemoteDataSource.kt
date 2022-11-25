package com.example.datatrack.data.datasource

import com.example.datatrack.data.api.Api
import com.example.datatrack.data.dto.StationDto
import com.example.domain.domain.usecase.GetStationsUseCase.Input
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: Api
) {
    fun getRemoteStations(input: Input): Flow<List<StationDto>> =
        flow {
            while (true) {
                emit(
                    api.getChargingStations(
                        latitude = input.lat,
                        longitude = input.lng,
                        distance = input.distance,
                        distanceUnit = input.distanceUnit,
                    )
                )
                delay(timeMillis = 30000)
            }
        }
}
