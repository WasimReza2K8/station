package com.example.featuretrack.ui.map.view

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
import com.example.core.ext.exhaustive
import com.example.feature_track.R
import com.example.feature_track.databinding.FragmentMapBinding
import com.example.featuretrack.model.StationClusterItem
import com.example.featuretrack.model.StationUiInfo
import com.example.featuretrack.ui.map.viewmodel.TrackContract
import com.example.featuretrack.ui.map.viewmodel.TrackViewModel
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
class TrackFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener {

    private lateinit var googleMap: GoogleMap
    private val binding: FragmentMapBinding by viewBinding(FragmentMapBinding::bind)
    private val viewModel: TrackViewModel by viewModels()

    private lateinit var cancellationTokenSource: CancellationTokenSource

    private lateinit var clusterManager: ClusterManager<StationClusterItem>

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    companion object {
        private const val MAP_INITIAL_ZOOM_LEVEL = 15f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.googleMap.onCreate(savedInstanceState)
        binding.googleMap.getMapAsync(this)
        binding.googleMap.onResume()

        binding.btnRetry.setOnClickListener {
            viewModel.onEvent(TrackContract.Event.OnRetry)
        }
        observeEffect()
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.sheet.bottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun observeEffect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is TrackContract.Effect.NetworkErrorEffect -> {
                            showSnackBar(getString(R.string.network_error))
                        }
                        is TrackContract.Effect.UnknownErrorEffect -> {
                            showSnackBar(getString(R.string.unknown_error))
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.container, text, Snackbar.LENGTH_LONG).show()
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    setLoading(viewState.isLoading)
                    viewState.nearestVehicle?.let { addNearestVehicleInfo(it) }
                    if (viewState.stationUiInfoList.isNotEmpty()) {
                        addItems(viewState.stationUiInfoList)
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

    private fun addNearestVehicleInfo(nearestVehicle: StationUiInfo) {
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        with(binding.sheet) {
            tvTitle.text = nearestVehicle.title
            tvAddressText.text = nearestVehicle.address
            tvNumberOfPoint.text = nearestVehicle.numberOfPoints.toString()
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
        clusterManager.renderer = TrackClusterRenderer(
            context = requireContext(),
            map = googleMap,
            clusterManager = clusterManager,
        )
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
    }

    private fun addItems(vehicleList: List<StationUiInfo>) {
        val clusterItems = vehicleList.map { it.clusterItem }
        clusterManager.addItems(clusterItems)
        clusterManager.cluster()
    }

    override fun onStart() {
        super.onStart()
        cancellationTokenSource = CancellationTokenSource()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnCameraIdleListener(this)
        setupCluster()
        observeViewState()
        addUserLocationMarker(
            LatLng(
                TrackViewModel.INITIAL_LATITUDE, TrackViewModel.INITIAL_LONGITUDE
            )
        )
    }

    private fun getMarkerManager(): MarkerManager {
        return object : MarkerManager(this.googleMap) {
            override fun onMarkerClick(marker: Marker): Boolean {
                viewModel.onEvent(TrackContract.Event.OnMarkerClicked(marker))
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

    private fun moveToMap(latLng: LatLng, mapZoom: Float = MAP_INITIAL_ZOOM_LEVEL) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latLng.latitude, latLng.longitude),
                mapZoom
            )
        )
    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
    }

    override fun onCameraIdle() {
        // do nothing
    }
}
