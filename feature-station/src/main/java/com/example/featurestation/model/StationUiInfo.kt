package com.example.featurestation.model

import com.example.core.resProvider.ResourceProvider
import com.example.domain.domain.model.StationInfo
import com.example.featurestation.R

data class StationUiInfo(
    val id: Int,
    val clusterItem: StationClusterItem,
    val address: String,
    val title: String,
    val numberOfPoints: String,
)

fun StationInfo.mapToStationUiInfo(resourceProvider: ResourceProvider) =
    StationUiInfo(
        id = id,
        clusterItem = StationClusterItem(
            lat = lat,
            lng = lng,
            titleString = title,
        ),
        title = title,
        numberOfPoints = numberOfPoints?.let {
            resourceProvider.getString(R.string.number_of_point, it)
        } ?: "",
        address = getAddress(
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            postCode = postCode,
            town = town,
        )
    )

private fun getAddress(
    addressLine1: String,
    addressLine2: String?,
    postCode: String,
    town: String?,
): String {
    var address = addressLine1
    if (addressLine2 != null) {
        address = "$address\n$addressLine2"
    }
    address = "$address\n$postCode"
    if (town != null) {
        address = "$address, $town"
    }
    return address
}
