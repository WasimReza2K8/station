package com.example.featuretrack.ui.map.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.core.ext.exhaustive
import com.example.core.state.Output
import com.example.core.viewmodel.BaseViewModel
import com.example.domain.domain.usecase.GetStationsUseCase
import com.example.featuretrack.model.mapToStationUiInfo
import com.example.featuretrack.ui.map.viewmodel.TrackContract.Event.OnInitViewModel
import com.example.featuretrack.ui.map.viewmodel.TrackContract.Event.OnMarkerClicked
import com.example.featuretrack.ui.map.viewmodel.TrackContract.Event.OnRetry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase
) : BaseViewModel<TrackContract.Event, TrackContract.State, TrackContract.Effect>() {

    companion object {
        const val INITIAL_LATITUDE = 52.526
        const val INITIAL_LONGITUDE = 13.415
        const val INITIAL_DISTANCE = 5
        const val INITIAL_DISTANCE_UNIT = "km"
    }

    init {
        onEvent(OnInitViewModel)
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getStationsUseCase(
                GetStationsUseCase.Input(
                    lat = INITIAL_LATITUDE,
                    lng = INITIAL_LONGITUDE,
                    distance = INITIAL_DISTANCE,
                    distanceUnit = INITIAL_DISTANCE_UNIT,
                )
            ).collect { output ->
                when (output) {
                    is Output.Success -> {
                        Timber.e("stations: ${output.result}")
                        val uiVehicleList = output.result.map {
                            it.mapToStationUiInfo()
                        }
                        updateState {
                            copy(stationUiInfoList = uiVehicleList)
                        }
                    }
                    is Output.NetworkError -> {
                        sendEffect { TrackContract.Effect.NetworkErrorEffect }
                    }
                    is Output.UnknownError -> {
                        sendEffect { TrackContract.Effect.UnknownErrorEffect("unknown error") }
                    }
                }
                updateState { copy(isLoading = false) }
            }
        }
    }

    override fun provideInitialState(): TrackContract.State {
        return TrackContract.State()
    }

    override fun handleEvent(event: TrackContract.Event) {
        when (event) {
            is OnInitViewModel, OnRetry -> loadData()
            is OnMarkerClicked -> {
                val items = viewState.value.stationUiInfoList.filter { stationUiInfo ->
                    stationUiInfo.clusterItem.position == event.marker.position
                }
                updateState {
                    copy(
                        nearestVehicle = items.first()
                    )
                }
            }
        }.exhaustive
    }
}
