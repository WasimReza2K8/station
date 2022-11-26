package com.example.featurestation.ui.map.view

import android.content.Context
import com.example.featurestation.R
import com.example.featurestation.model.StationClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class StationClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<StationClusterItem>
) : DefaultClusterRenderer<StationClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: StationClusterItem,
        markerOptions: MarkerOptions,
    ) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_charging_station))
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}
