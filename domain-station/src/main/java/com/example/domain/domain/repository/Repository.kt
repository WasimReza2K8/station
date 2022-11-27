package com.example.domain.domain.repository

import com.example.domain.domain.model.StationInfo
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getStations(
        lat: Double,
        lng: Double,
        distance: Int,
        distanceUnit: String,
    ): Flow<List<StationInfo>>
}
