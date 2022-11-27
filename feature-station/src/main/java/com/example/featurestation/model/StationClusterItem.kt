package com.example.featurestation.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class StationClusterItem(
    val lat: Double,
    val lng: Double,
    val titleString: String,
) : ClusterItem {
    private val position: LatLng = LatLng(lat, lng)

    override fun getPosition(): LatLng = position

    override fun getTitle(): String = titleString

    override fun getSnippet(): String? = null
}
