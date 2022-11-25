package com.example.domain.domain.model

data class StationInfo(
    val id: Int,
    val title: String,
    val addressLine1: String,
    val addressLine2: String?,
    val postCode: String,
    val town: String?,
    val lat: Double,
    val lng: Double,
    val numberOfPoints: Int?,
)
