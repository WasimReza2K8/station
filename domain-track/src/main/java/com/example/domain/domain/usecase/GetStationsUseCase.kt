package com.example.domain.domain.usecase

import com.example.core.state.Output
import com.example.domain.domain.model.StationInfo
import kotlinx.coroutines.flow.Flow

interface GetStationsUseCase {
    data class Input(
        val lat: Double,
        val lng: Double,
        val distance: Int,
        val distanceUnit: String,
    )

    operator fun invoke(input: Input): Flow<Output<List<StationInfo>>>
}
