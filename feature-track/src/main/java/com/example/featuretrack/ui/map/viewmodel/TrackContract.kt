package com.example.featuretrack.ui.map.viewmodel

import com.example.core.viewmodel.ViewEffect
import com.example.core.viewmodel.ViewEvent
import com.example.core.viewmodel.ViewState
import com.example.featuretrack.model.StationUiInfo
import com.google.android.gms.maps.model.Marker

class TrackContract {
    data class State(
        val isLoading: Boolean = false,
        val stationUiInfoList: List<StationUiInfo> = emptyList(),
        val nearestVehicle: StationUiInfo? = null
    ) : ViewState

    sealed interface Effect : ViewEffect {
        data class UnknownErrorEffect(val message: String) : Effect
        object NetworkErrorEffect : Effect
    }

    sealed interface Event : ViewEvent {
        object OnInitViewModel : Event
        object OnRetry : Event
        data class OnMarkerClicked(val marker: Marker) : Event
    }
}
