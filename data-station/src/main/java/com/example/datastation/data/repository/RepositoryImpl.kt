package com.example.datastation.data.repository

import com.example.datastation.data.datasource.RemoteDataSource
import com.example.datastation.data.dto.mapToStationInfo
import com.example.domain.domain.model.StationInfo
import com.example.domain.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) : Repository {
    override fun getStations(
        lat: Double,
        lng: Double,
        distance: Int,
        distanceUnit: String,
    ): Flow<List<StationInfo>> =
        remoteDataSource.getRemoteStations(
            lat = lat,
            lng = lng,
            distance = distance,
            distanceUnit = distanceUnit,
        ).map { stationDtoItems ->
            stationDtoItems.map { stationDtoItem ->
                stationDtoItem.mapToStationInfo()
            }
        }
}
