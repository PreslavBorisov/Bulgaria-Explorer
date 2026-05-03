package com.bulgariaexplorer.app.ui.admin.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminAchievementRequest
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.databinding.FragmentAchievementFormBinding
import com.bulgariaexplorer.app.utils.NavArgs

class AchievementFormFragment : Fragment() {

    private var _binding: FragmentAchievementFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminAchievementsViewModel by viewModels {
        AdminAchievementsViewModelFactory(AdminRepository())
    }

    private var editAchievementId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editAchievementId = arguments?.getLong(NavArgs.ACHIEVEMENT_ID)?.takeIf { it > 0 }

        setupListeners()
        observeViewModel()

        if (editAchievementId != null) {
            binding.tvFormTitle.text = getString(R.string.achievement_form_title_edit)
            loadExistingAchievement()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveAchievement()
        }
    }

    private fun loadExistingAchievement() {
        viewModel.loadAchievements()
        viewModel.achievementsState.observe(viewLifecycleOwner) { state ->
            if (state is AdminAchievementsViewModel.AchievementsState.Success) {
                val achievement = state.achievements.find { it.id == editAchievementId }
                achievement?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description ?: "")
                    binding.etCode.setText(it.code)
                    binding.etIconName.setText(it.iconName ?: "")
                    binding.etTargetValue.setText(it.targetValue?.toString() ?: "")
                    binding.switchActive.isChecked = it.active
                }
            }
        }
    }

    private fun saveAchievement() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val code = binding.etCode.text.toString().trim()
        val iconName = binding.etIconName.text.toString().trim().ifEmpty { null }
        val targetValue = binding.etTargetValue.text.toString().toIntOrNull()
        val active = binding.switchActive.isChecked

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.field_title_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (code.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.achievement_form_code_required), Toast.LENGTH_SHORT).show()
            return
        }

        val request = AdminAchievementRequest(
            title = title,
            description = description.ifEmpty { null },
            code = code,
            iconName = iconName,
            targetValue = targetValue,
            active = active
        )

        if (editAchievementId != null) {
            viewModel.updateAchievement(editAchievementId!!, request)
        } else {
            viewModel.createAchievement(request)
        }
    }

    private fun observeViewModel() {
        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminAchievementsViewModel.FormState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }
                is AdminAchievementsViewModel.FormState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is AdminAchievementsViewModel.FormState.Error -> {
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
