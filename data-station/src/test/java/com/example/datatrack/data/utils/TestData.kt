package com.example.datatrack.data.utils

import com.example.datatrack.data.dto.AddressInfoDto
import com.example.datatrack.data.dto.StationDto
import com.example.domain.domain.model.StationInfo

object TestData {
    private val addressInfoDto = AddressInfoDto(
        latitude = 53.02,
        longitude = 13.32,
        id = 12,
        addressLine1 = "address1",
        addressLine2 = "address2",
        postcode = "postCode",
        town = "town",
        title = "test title"
    )

    val stationDto = StationDto(
        numberOfPoints = 2,
        id = 1,
        addressInfo = addressInfoDto
    )

    val responseDto = arrayListOf(stationDto)

    val stationInfo = StationInfo(
        id = 1,
        title = "test title",
        addressLine1 = "address1",
        addressLine2 = "address2",
        postCode = "postCode",
        town = "town",
        lat = 53.02,
        lng = 13.32,
        numberOfPoints = 2,
    )
}
