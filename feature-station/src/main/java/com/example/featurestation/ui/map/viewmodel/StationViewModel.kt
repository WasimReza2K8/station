package com.example.featurestation.ui.map.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output
import com.example.core.viewmodel.BaseViewModel
import com.example.domain.domain.usecase.GetStationsUseCase
import com.example.featurestation.model.mapToStationUiInfo
import com.example.featurestation.ui.map.viewmodel.StationContract.Effect.NetworkErrorEffect
import com.example.featurestation.ui.map.viewmodel.StationContract.Effect.UnknownErrorEffect
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnMapClicked
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnMarkerClicked
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnRetry
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnViewStarted
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnViewStopped
import com.example.featurestation.utils.Constants.INITIAL_DISTANCE
import com.example.featurestation.utils.Constants.INITIAL_DISTANCE_UNIT
import com.example.featurestation.utils.Constants.INITIAL_LATITUDE
import com.example.featurestation.utils.Constants.INITIAL_LONGITUDE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<StationContract.Event, StationContract.State, StationContract.Effect>() {

    @VisibleForTesting
    var job: Job? = null

    override fun provideInitialState(): StationContract.State = StationContract.State()

    private fun getChargingStations() {
        job = viewModelScope.launch {
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
                        val uiVehicleList = output.result.map {
                            it.mapToStationUiInfo(resourceProvider)
                        }
                        updateState {
                            copy(stationUiInfoList = uiVehicleList)
                        }
                    }
                    is Output.NetworkError -> {
                        sendEffect { NetworkErrorEffect }
                    }
                    is Output.UnknownError -> {
                        sendEffect { UnknownErrorEffect }
                    }
                }
                updateState { copy(isLoading = false) }
            }
        }
    }

    override fun handleEvent(event: StationContract.Event) {
        when (event) {
            is OnViewStarted, OnRetry -> getChargingStations()

            is OnMarkerClicked -> {
                val items = viewState.value.stationUiInfoList.filter { stationUiInfo ->
                    stationUiInfo.clusterItem.position == event.marker.position
                }
                updateState {
                    copy(
                        selectedStation = items.firstOrNull()
                    )
                }
            }
            is OnMapClicked -> {
                updateState {
                    copy(
                        selectedStation = null
                    )
                }
            }
            is OnViewStopped -> job?.cancel()
        }
    }
}
