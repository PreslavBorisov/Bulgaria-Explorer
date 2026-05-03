package com.bulgariaexplorer.app.ui.visits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.repository.VisitRepository
import com.bulgariaexplorer.app.databinding.FragmentMyVisitsBinding
import com.bulgariaexplorer.app.utils.NavArgs

class MyVisitsFragment : Fragment() {

    private var _binding: FragmentMyVisitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyVisitsViewModel by viewModels {
        MyVisitsViewModelFactory(VisitRepository(requireContext()))
    }

    private lateinit var visitAdapter: VisitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyVisitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        visitAdapter = VisitAdapter { visit ->
            val bundle = Bundle().apply {
                putLong(NavArgs.POI_ID, visit.poiId)
            }
            findNavController().navigate(R.id.action_myVisitsFragment_to_poiDetailsFragment, bundle)
        }

        binding.rvVisits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = visitAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.visitsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MyVisitsViewModel.VisitsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvVisits.visibility = View.GONE
                    binding.emptyState.visibility = View.GONE
                }
                is MyVisitsViewModel.VisitsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (state.visits.isEmpty()) {
                        binding.rvVisits.visibility = View.GONE
                        binding.emptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvVisits.visibility = View.VISIBLE
                        binding.emptyState.visibility = View.GONE
                        visitAdapter.submitList(state.visits)
                    }
                }
                is MyVisitsViewModel.VisitsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvVisits.visibility = View.GONE
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
