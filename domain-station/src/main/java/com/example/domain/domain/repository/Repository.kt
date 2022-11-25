package com.example.domain.domain.repository

import com.example.domain.domain.model.StationInfo
import com.example.domain.domain.usecase.GetStationsUseCase.Input
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getStations(input: Input): Flow<List<StationInfo>>
}
