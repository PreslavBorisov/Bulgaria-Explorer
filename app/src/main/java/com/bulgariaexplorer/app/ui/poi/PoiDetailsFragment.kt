package com.bulgariaexplorer.app.ui.poi

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.VisitRepository
import com.bulgariaexplorer.app.databinding.FragmentPoiDetailsBinding
import com.bulgariaexplorer.app.utils.NavArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class PoiDetailsFragment : Fragment() {

    private var _binding: FragmentPoiDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoUri: Uri? = null

    private val viewModel: PoiDetailsViewModel by viewModels {
        PoiDetailsViewModelFactory(
            PoiRepository(),
            FavoriteRepository(),
            VisitRepository(requireContext())
        )
    }

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            takePhoto()
        } else {
            Toast.makeText(requireContext(), getString(R.string.poi_details_permissions_required), Toast.LENGTH_SHORT).show()
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            viewModel.checkInWithPhoto(photoUri!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoiDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val poiId = arguments?.getLong(NavArgs.POI_ID) ?: return
        viewModel.loadPoi(poiId)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnVisit.setOnClickListener {
            checkPermissionsAndTakePhoto()
        }
    }

    private fun checkPermissionsAndTakePhoto() {
        val cameraGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val locationGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (cameraGranted && locationGranted) {
            takePhoto()
        } else {
            cameraPermissionRequest.launch(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }

    private fun takePhoto() {
        val photoFile = File(requireContext().cacheDir, "visit_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        takePicture.launch(photoUri)
    }

    private fun observeViewModel() {
        viewModel.poiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PoiDetailsViewModel.PoiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PoiDetailsViewModel.PoiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    displayPoi(state.poi)
                }
                is PoiDetailsViewModel.PoiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.favoriteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PoiDetailsViewModel.FavoriteState.IsFavorite -> {
                    updateFavoriteButton(state.isFavorite)
                }
                is PoiDetailsViewModel.FavoriteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        viewModel.visitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PoiDetailsViewModel.VisitState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnVisit.isEnabled = false
                }
                is PoiDetailsViewModel.VisitState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVisit.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.poi_details_visit_success, state.visit.totalAwardedPoints),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is PoiDetailsViewModel.VisitState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVisit.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnVisit.isEnabled = true
                }
            }
        }
    }

    private fun displayPoi(poi: com.bulgariaexplorer.app.data.remote.dto.PoiResponse) {
        binding.apply {
            tvPoiName.text = poi.title ?: getString(R.string.poi_details_unknown_place)
            tvPoiDescription.text = poi.fullDescription ?: poi.shortDescription ?: getString(R.string.poi_details_no_description)
            tvPoiCity.text = poi.city ?: getString(R.string.poi_details_unknown_city)
            tvPoiPoints.text = getString(R.string.poi_details_points_format, poi.rewardPoints)
            tvVisitCount.text = (poi.visitCount ?: 0).toString()
            tvDifficulty.text = poi.difficulty ?: getString(R.string.poi_details_difficulty_default)

            if (!poi.imageUrl.isNullOrEmpty()) {
                ivPoiImage.load(poi.imageUrl) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher)
                    error(R.mipmap.ic_launcher)
                }
            } else {
                ivPoiImage.setImageResource(R.mipmap.ic_launcher)
            }

            // Photo gallery
            val allPhotos = mutableListOf<String>()
            poi.imageUrls?.let { allPhotos.addAll(it) }
            poi.visitPhotoUrls?.let { allPhotos.addAll(it) }

            if (allPhotos.isNotEmpty()) {
                tvPhotosHeader.visibility = View.VISIBLE
                rvPhotos.visibility = View.VISIBLE
                rvPhotos.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false
                )
                rvPhotos.adapter = PhotoGalleryAdapter(allPhotos) { imageUrl ->
                    val bundle = Bundle().apply {
                        putString(NavArgs.IMAGE_URL, imageUrl)
                    }
                    findNavController().navigate(R.id.photoViewerFragment, bundle)
                }
            }
        }

        // Get current location for visit
        viewModel.updateCurrentLocation(fusedLocationClient)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorite) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
