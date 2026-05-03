package com.bulgariaexplorer.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.databinding.FragmentMapBinding
import com.bulgariaexplorer.app.utils.NavArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(PoiRepository())
    }

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                enableMyLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                enableMyLocation()
            }
            else -> {
                Toast.makeText(requireContext(), getString(R.string.map_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.fabMyLocation.setOnClickListener {
            moveToCurrentLocation()
        }
    }

    private fun observeViewModel() {
        viewModel.poisState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MapViewModel.PoiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is MapViewModel.PoiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    displayPoisOnMap(state.pois)
                }
                is MapViewModel.PoiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable zoom controls and gestures
        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
            isMyLocationButtonEnabled = false // We use custom FAB
        }

        // Set Bulgaria as default center
        val bulgaria = LatLng(42.7339, 25.4858)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(bulgaria, 7f))

        // Set marker click listener
        googleMap?.setOnMarkerClickListener { marker ->
            val poiId = marker.tag as? Long
            if (poiId != null) {
                navigateToPoiDetails(poiId)
            }
            true
        }

        checkLocationPermission()
    }

    private fun displayPoisOnMap(pois: List<com.bulgariaexplorer.app.data.remote.dto.PoiResponse>) {
        googleMap?.clear()
        pois.forEach { poi ->
            val position = LatLng(poi.latitude, poi.longitude)
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(poi.title ?: getString(R.string.map_unknown_title))
                    .snippet(poi.city ?: "")
            )
            marker?.tag = poi.id
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun enableMyLocation() {
        try {
            googleMap?.isMyLocationEnabled = true
        } catch (_: SecurityException) {
            Toast.makeText(requireContext(), getString(R.string.map_permission_required), Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                } ?: run {
                    Toast.makeText(requireContext(), getString(R.string.map_location_unavailable), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (_: SecurityException) {
            Toast.makeText(requireContext(), getString(R.string.map_permission_required), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToPoiDetails(poiId: Long) {
        val action = R.id.action_mapFragment_to_poiDetailsFragment
        val bundle = Bundle().apply {
            putLong(NavArgs.POI_ID, poiId)
        }
        findNavController().navigate(action, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
