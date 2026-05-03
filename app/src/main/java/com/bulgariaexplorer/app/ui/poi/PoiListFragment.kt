package com.bulgariaexplorer.app.ui.poi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.databinding.FragmentPoiListBinding
import com.bulgariaexplorer.app.utils.NavArgs

class PoiListFragment : Fragment() {

    private var _binding: FragmentPoiListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PoiViewModel by viewModels {
        PoiViewModelFactory(PoiRepository())
    }

    private lateinit var poiAdapter: PoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoiListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        poiAdapter = PoiAdapter { poi ->
            // Navigate to POI details
            val action = R.id.action_poiListFragment_to_poiDetailsFragment
            val bundle = Bundle().apply {
                putLong(NavArgs.POI_ID, poi.id)
            }
            findNavController().navigate(action, bundle)
        }

        binding.rvPois.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = poiAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchPois(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.poisState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PoiViewModel.PoiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvPois.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                is PoiViewModel.PoiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (state.pois.isEmpty()) {
                        binding.rvPois.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = getString(R.string.poi_list_no_results)
                    } else {
                        binding.rvPois.visibility = View.VISIBLE
                        binding.tvError.visibility = View.GONE
                        poiAdapter.submitList(state.pois)
                    }
                }
                is PoiViewModel.PoiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvPois.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
