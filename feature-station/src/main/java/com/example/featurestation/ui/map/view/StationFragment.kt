package com.example.featurestation.ui.map.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.core.delegate.viewBinding
import com.example.featurestation.R
import com.example.featurestation.databinding.FragmentMapBinding
import com.example.featurestation.model.StationClusterItem
import com.example.featurestation.model.StationUiInfo
import com.example.featurestation.ui.map.viewmodel.StationContract
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnErrorMessageShown
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnMapClicked
import com.example.featurestation.ui.map.viewmodel.StationContract.Event.OnViewStarted
import com.example.featurestation.ui.map.viewmodel.StationViewModel
import com.example.featurestation.utils.Constants.INITIAL_LATITUDE
import com.example.featurestation.utils.Constants.INITIAL_LONGITUDE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StationFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val binding: FragmentMapBinding by viewBinding(FragmentMapBinding::bind)
    private val viewModel: StationViewModel by viewModels()

    private lateinit var cancellationTokenSource: CancellationTokenSource

    private lateinit var clusterManager: ClusterManager<StationClusterItem>

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    companion object {
        private const val MAP_INITIAL_ZOOM_LEVEL = 15f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.googleMap.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@StationFragment)
            onResume()
        }
        binding.btnRetry.setOnClickListener {
            viewModel.onEvent(StationContract.Event.OnRetry)
        }
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.sheet.bottomSheet)
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.container, text, Snackbar.LENGTH_LONG).show()
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    setLoading(viewState.isLoading)
                    if (viewState.stationUiInfoList.isNotEmpty()) {
                        addItems(viewState.stationUiInfoList)
                    }
                    val selectedStation = viewState.selectedStation
                    if (selectedStation != null) {
                        selectStation(selectedStation)
                    } else {
                        hideBottomSheet()
                    }
                    viewState.errorMessage?.let { error ->
                        showSnackBar(error)
                        viewModel.onEvent(OnErrorMessageShown)
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.GONE
        }
    }

    private fun selectStation(selectedStation: StationUiInfo) {
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        with(binding.sheet) {
            tvTitle.text = selectedStation.title
            tvAddressText.text = selectedStation.address
            if (selectedStation.numberOfPoints.isNotEmpty()) {
                tvNumberOfPoint.visibility = View.VISIBLE
                tvNumberOfPoint.text = selectedStation.numberOfPoints
            } else {
                tvNumberOfPoint.visibility = View.GONE
            }
        }
    }

    private fun hideBottomSheet() {
        if (standardBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setupCluster() {
        val markerManager = getMarkerManager()
        clusterManager = ClusterManager(
            requireContext(),
            googleMap,
            markerManager
        )
        clusterManager.renderer = StationClusterRenderer(
            context = requireContext(),
            map = googleMap,
            clusterManager = clusterManager,
        )
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        googleMap.setOnMapClickListener {
            viewModel.onEvent(OnMapClicked)
        }
    }

    private fun addItems(stationUiInfoList: List<StationUiInfo>) {
        val clusterItems = stationUiInfoList.map { it.clusterItem }
        clusterManager.addItems(clusterItems)
        clusterManager.cluster()
    }

    override fun onStart() {
        super.onStart()
        cancellationTokenSource = CancellationTokenSource()
        viewModel.onEvent(OnViewStarted)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupCluster()
        observeViewState()
        addUserLocationMarker(
            LatLng(
                INITIAL_LATITUDE,
                INITIAL_LONGITUDE,
            )
        )
    }

    private fun getMarkerManager(): MarkerManager {
        return object : MarkerManager(this.googleMap) {
            override fun onMarkerClick(marker: Marker): Boolean {
                viewModel.onEvent(StationContract.Event.OnMarkerClicked(marker))
                return super.onMarkerClick(marker)
            }
        }
    }

    private fun addUserLocationMarker(latLng: LatLng) {
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.your_position))
        )
        moveToMap(latLng)
    }

    private fun moveToMap(latLng: LatLng) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latLng.latitude, latLng.longitude),
                MAP_INITIAL_ZOOM_LEVEL
            )
        )
    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
        viewModel.onEvent(StationContract.Event.OnViewStopped)
    }
}
