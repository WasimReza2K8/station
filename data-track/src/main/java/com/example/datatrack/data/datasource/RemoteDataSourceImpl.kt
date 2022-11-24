package com.example.datatrack.data.datasource

import com.example.datatrack.data.api.Api
import com.example.datatrack.data.dto.StationDtoItem
import com.example.domain.domain.usecase.GetStationsUseCase.Input
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val api: Api) : RemoteDataSource {

    override fun getRemoteStations(input: Input): Flow<List<StationDtoItem>> =
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
                delay(30000)
            }
        }
}
