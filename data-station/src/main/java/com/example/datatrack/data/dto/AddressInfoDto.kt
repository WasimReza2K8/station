package com.example.datatrack.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressInfoDto(
    @SerialName("AddressLine1")
    val addressLine1: String,
    @SerialName("AddressLine2")
    val addressLine2: String?,
    @SerialName("ID")
    val id: Int,
    @SerialName("Latitude")
    val latitude: Double,
    @SerialName("Longitude")
    val longitude: Double,
    @SerialName("Postcode")
    val postcode: String,
    @SerialName("Title")
    val title: String,
    @SerialName("Town")
    val town: String?
)
