package com.bulgariaexplorer.app.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.repository.FavoriteRepository
import com.bulgariaexplorer.app.databinding.FragmentFavoritesBinding
import com.bulgariaexplorer.app.utils.NavArgs
import com.bulgariaexplorer.app.ui.poi.PoiAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(FavoriteRepository())
    }

    private lateinit var poiAdapter: PoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        poiAdapter = PoiAdapter { poi ->
            // Navigate to POI details
            val action = R.id.action_favoritesFragment_to_poiDetailsFragment
            val bundle = Bundle().apply {
                putLong(NavArgs.POI_ID, poi.id)
            }
            findNavController().navigate(action, bundle)
        }

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = poiAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.favoritesState.observe(viewLifecycleOwner) { state ->
            Log.d("FavoritesFragment", "State: $state")
            when (state) {
                is FavoritesViewModel.FavoritesState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                    binding.emptyState.visibility = View.GONE
                }
                is FavoritesViewModel.FavoritesState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("FavoritesFragment", "Favorites: ${state.favorites.size}")
                    if (state.favorites.isEmpty()) {
                        binding.rvFavorites.visibility = View.GONE
                        binding.emptyState.visibility = View.VISIBLE
                    } else {
                        // Convert FavoriteResponse to PoiResponse for display
                        val pois = state.favorites.map { fav ->
                            PoiResponse(
                                id = fav.poiId,
                                title = fav.poiTitle,
                                shortDescription = null,
                                fullDescription = null,
                                latitude = 0.0,
                                longitude = 0.0,
                                rewardPoints = fav.rewardPoints ?: 0,
                                imageUrl = fav.imageUrl,
                                imageUrls = null,
                                visitPhotoUrls = null,
                                categoryName = null,
                                categoryCode = null,
                                address = null,
                                city = fav.city,
                                region = null,
                                active = true
                            )
                        }
                        Log.d("FavoritesFragment", "POIs created: ${pois.size}")
                        binding.rvFavorites.visibility = View.VISIBLE
                        binding.emptyState.visibility = View.GONE
                        poiAdapter.submitList(pois)
                    }
                }
                is FavoritesViewModel.FavoritesState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvFavorites.visibility = View.GONE
                    binding.emptyState.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
