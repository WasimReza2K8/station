package com.example.featuretrack.model

import com.example.domain.domain.model.StationInfo

data class StationUiInfo(
    val id: Int,
    val clusterItem: StationClusterItem,
    val address: String,
    val title: String,
    val numberOfPoints: Int,
)

fun StationInfo.mapToStationUiInfo() =
    StationUiInfo(
        id = id,
        clusterItem = StationClusterItem(
            lat = lat,
            lng = lng,
            titleString = title,
            snippetString = "",
        ),
        title = title,
        numberOfPoints = numberOfPoints ?: 0,
        address = if (addressLine2 != null) {
            "${addressLine1}\n${addressLine2}\n$postCode, $town"
        } else {
            "${addressLine1}\n$postCode, $town"
        }
    )
