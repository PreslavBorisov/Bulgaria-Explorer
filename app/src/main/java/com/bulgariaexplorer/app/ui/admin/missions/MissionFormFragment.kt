package com.bulgariaexplorer.app.ui.admin.missions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminMissionRequest
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.databinding.FragmentMissionFormBinding
import com.bulgariaexplorer.app.utils.NavArgs

class MissionFormFragment : Fragment() {

    private var _binding: FragmentMissionFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminMissionsViewModel by viewModels {
        AdminMissionsViewModelFactory(AdminRepository())
    }

    private var editMissionId: Long? = null

    private val missionTypes = listOf(
        "VISIT_COUNT", "CATEGORY_VISIT", "REGION_VISIT", "DAILY_LOGIN", "STREAK"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editMissionId = arguments?.getLong(NavArgs.MISSION_ID)?.takeIf { it > 0 }

        setupDropdown()
        setupListeners()
        observeViewModel()

        if (editMissionId != null) {
            binding.tvFormTitle.text = getString(R.string.mission_form_title_edit)
            loadExistingMission()
        }
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, missionTypes)
        binding.actvMissionType.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveMission()
        }
    }

    private fun loadExistingMission() {
        viewModel.loadMissions()
        viewModel.missionsState.observe(viewLifecycleOwner) { state ->
            if (state is AdminMissionsViewModel.MissionsState.Success) {
                val mission = state.missions.find { it.id == editMissionId }
                mission?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description ?: "")
                    binding.actvMissionType.setText(it.missionType, false)
                    binding.etTargetValue.setText(it.targetValue.toString())
                    binding.etRewardPoints.setText(it.rewardPoints.toString())
                    binding.etRegion.setText(it.region ?: "")
                    binding.switchActive.isChecked = it.active
                }
            }
        }
    }

    private fun saveMission() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val missionType = binding.actvMissionType.text.toString().trim()
        val targetValue = binding.etTargetValue.text.toString().toIntOrNull()
        val rewardPoints = binding.etRewardPoints.text.toString().toIntOrNull() ?: 0
        val region = binding.etRegion.text.toString().trim().ifEmpty { null }
        val active = binding.switchActive.isChecked

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.field_title_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (missionType.isEmpty() || missionType !in missionTypes) {
            Toast.makeText(requireContext(), getString(R.string.mission_form_type_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (targetValue == null) {
            Toast.makeText(requireContext(), getString(R.string.mission_form_target_required), Toast.LENGTH_SHORT).show()
            return
        }

        val request = AdminMissionRequest(
            title = title,
            description = description.ifEmpty { null },
            missionType = missionType,
            targetValue = targetValue,
            rewardPoints = rewardPoints,
            categoryId = null,
            region = region,
            validFrom = null,
            validTo = null,
            active = active
        )

        if (editMissionId != null) {
            viewModel.updateMission(editMissionId!!, request)
        } else {
            viewModel.createMission(request)
        }
    }

    private fun observeViewModel() {
        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminMissionsViewModel.FormState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }
                is AdminMissionsViewModel.FormState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is AdminMissionsViewModel.FormState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
