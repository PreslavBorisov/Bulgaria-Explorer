package com.bulgariaexplorer.app.ui.missions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.data.repository.MissionRepository
import com.bulgariaexplorer.app.databinding.FragmentMissionsBinding

class MissionsFragment : Fragment() {

    private var _binding: FragmentMissionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MissionsViewModel by viewModels {
        MissionsViewModelFactory(MissionRepository())
    }

    private lateinit var adapter: MissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MissionAdapter()
        binding.rvMissions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MissionsFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.missionsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MissionsViewModel.MissionsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvMissions.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                }
                is MissionsViewModel.MissionsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (state.missions.isEmpty()) {
                        binding.rvMissions.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvMissions.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        adapter.submitList(state.missions)
                    }
                }
                is MissionsViewModel.MissionsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvMissions.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
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
