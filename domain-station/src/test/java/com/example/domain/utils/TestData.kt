package com.example.domain.utils

import com.example.domain.domain.model.StationInfo

object TestData {
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

    val stations = listOf(
        stationInfo.copy(id = 2, lat = 100.5, lng = 32.6),
        stationInfo.copy(id = 3, lat = 10.5, lng = 54.6),
        stationInfo,
    )
}
