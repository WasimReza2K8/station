package com.example.datatrack.data.dto

import com.example.domain.domain.model.StationInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationDtoItem(
    @SerialName("AddressInfo")
    val addressInfo: AddressInfo,
    @SerialName("NumberOfPoints")
    val numberOfPoints: Int?,
    @SerialName("ID")
    val id: Int,
)

fun StationDtoItem.mapToStationInfo() =
    StationInfo(
        id = id,
        title = addressInfo.title,
        lat = addressInfo.latitude,
        lng = addressInfo.longitude,
        numberOfPoints = numberOfPoints,
        addressLine1 = addressInfo.addressLine1,
        addressLine2 = addressInfo.addressLine2,
        postCode = addressInfo.postcode,
        town = addressInfo.town,
    )