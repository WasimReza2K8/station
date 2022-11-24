package com.example.datatrack.data.api

import com.example.datatrack.data.dto.StationDtoItem
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("v3/poi/")
    suspend fun getChargingStations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: Int,
        @Query("distanceunit") distanceUnit: String,
    ): List<StationDtoItem>
}
