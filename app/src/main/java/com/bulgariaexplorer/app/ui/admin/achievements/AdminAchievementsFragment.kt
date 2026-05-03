package com.bulgariaexplorer.app.ui.admin.achievements

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
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminAchievementResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.databinding.FragmentAdminAchievementsBinding
import com.bulgariaexplorer.app.utils.NavArgs

class AdminAchievementsFragment : Fragment() {

    private var _binding: FragmentAdminAchievementsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminAchievementsViewModel by viewModels {
        AdminAchievementsViewModelFactory(AdminRepository())
    }

    private lateinit var adapter: AdminAchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadAchievements()
    }

    private fun setupRecyclerView() {
        adapter = AdminAchievementAdapter(emptyList()) { achievement ->
            showAchievementActionDialog(achievement)
        }
        binding.rvAchievements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAchievements.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.achievementFormFragment)
        }
    }

    private fun showAchievementActionDialog(achievement: AdminAchievementResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(achievement.title)
            .setItems(arrayOf(getString(R.string.action_edit), getString(R.string.dialog_delete_confirm))) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply { putLong(NavArgs.ACHIEVEMENT_ID, achievement.id) }
                        findNavController().navigate(R.id.achievementFormFragment, bundle)
                    }
                    1 -> confirmDelete(achievement)
                }
            }
            .show()
    }

    private fun confirmDelete(achievement: AdminAchievementResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.admin_achievement_delete_confirm, achievement.title))
            .setPositiveButton(getString(R.string.dialog_delete_confirm)) { _, _ -> viewModel.deleteAchievement(achievement.id) }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.achievementsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminAchievementsViewModel.AchievementsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is AdminAchievementsViewModel.AchievementsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateAchievements(state.achievements)
                    binding.tvEmpty.visibility = if (state.achievements.isEmpty()) View.VISIBLE else View.GONE
                }
                is AdminAchievementsViewModel.AchievementsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminAchievementsViewModel.FormState.Success ->
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                is AdminAchievementsViewModel.FormState.Error ->
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
