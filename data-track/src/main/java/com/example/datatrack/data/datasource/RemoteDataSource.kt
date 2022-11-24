package com.example.datatrack.data.datasource

import com.example.datatrack.data.dto.StationDtoItem
import com.example.domain.domain.usecase.GetStationsUseCase.Input
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getRemoteStations(input: Input): Flow<List<StationDtoItem>>
}
