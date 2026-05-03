package com.bulgariaexplorer.app.ui.admin.missions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminMissionResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.databinding.FragmentAdminMissionsBinding
import com.bulgariaexplorer.app.utils.NavArgs

class AdminMissionsFragment : Fragment() {

    private var _binding: FragmentAdminMissionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminMissionsViewModel by viewModels {
        AdminMissionsViewModelFactory(AdminRepository())
    }

    private lateinit var adapter: AdminMissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadMissions()
    }

    private fun setupRecyclerView() {
        adapter = AdminMissionAdapter(emptyList()) { mission ->
            showMissionActionDialog(mission)
        }
        binding.rvMissions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMissions.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.missionFormFragment)
        }
    }

    private fun showMissionActionDialog(mission: AdminMissionResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(mission.title)
            .setItems(arrayOf(getString(R.string.action_edit), getString(R.string.dialog_delete_confirm))) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply { putLong(NavArgs.MISSION_ID, mission.id) }
                        findNavController().navigate(R.id.missionFormFragment, bundle)
                    }
                    1 -> confirmDelete(mission)
                }
            }
            .show()
    }

    private fun confirmDelete(mission: AdminMissionResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.admin_mission_delete_confirm, mission.title))
            .setPositiveButton(getString(R.string.dialog_delete_confirm)) { _, _ -> viewModel.deleteMission(mission.id) }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.missionsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminMissionsViewModel.MissionsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is AdminMissionsViewModel.MissionsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateMissions(state.missions)
                    binding.tvEmpty.visibility = if (state.missions.isEmpty()) View.VISIBLE else View.GONE
                }
                is AdminMissionsViewModel.MissionsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminMissionsViewModel.FormState.Success ->
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                is AdminMissionsViewModel.FormState.Error ->
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
