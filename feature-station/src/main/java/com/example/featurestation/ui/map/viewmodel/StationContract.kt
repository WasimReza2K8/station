package com.example.featurestation.ui.map.viewmodel

import com.example.core.viewmodel.ViewEvent
import com.example.core.viewmodel.ViewState
import com.example.featurestation.model.StationUiInfo
import com.google.android.gms.maps.model.Marker

class StationContract {
    data class State(
        val isLoading: Boolean = false,
        val stationUiInfoList: List<StationUiInfo> = emptyList(),
        val selectedStation: StationUiInfo? = null,
        val errorMessage: String? = null,
    ) : ViewState

    sealed interface Event : ViewEvent {
        object OnRetry : Event
        object OnMapClicked : Event
        data class OnMarkerClicked(val marker: Marker) : Event
        object OnViewStopped : Event
        object OnViewStarted : Event
        object OnErrorMessageShown : Event
    }
}
