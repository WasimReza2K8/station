package com.example.datastation.data.datasource

import com.example.datastation.data.api.Api
import com.example.datastation.data.dto.StationDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val api: Api) {
    fun getRemoteStations(
        lat: Double,
        lng: Double,
        distance: Int,
        distanceUnit: String,
    ): Flow<List<StationDto>> =
        flow {
            while (true) {
                emit(
                    api.getChargingStations(
                        latitude = lat,
                        longitude = lng,
                        distance = distance,
                        distanceUnit = distanceUnit,
                    )
                )
                delay(timeMillis = 30000)
            }
        }
}
