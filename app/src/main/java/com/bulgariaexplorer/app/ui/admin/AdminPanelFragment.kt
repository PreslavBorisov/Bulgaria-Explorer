package com.bulgariaexplorer.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.remote.dto.admin.SendNotificationRequest
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.databinding.FragmentAdminPanelBinding
import kotlinx.coroutines.launch

class AdminPanelFragment : Fragment() {

    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminPanelViewModel by viewModels {
        AdminPanelViewModelFactory(AdminRepository())
    }

    private val adminRepository by lazy { AdminRepository() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
        viewModel.loadStats()
    }

    private fun setupListeners() {
        binding.btnManageUsers.setOnClickListener {
            findNavController().navigate(R.id.adminUsersFragment)
        }
        binding.btnManagePois.setOnClickListener {
            findNavController().navigate(R.id.adminPoisFragment)
        }
        binding.btnManageMissions.setOnClickListener {
            findNavController().navigate(R.id.adminMissionsFragment)
        }
        binding.btnManageAchievements.setOnClickListener {
            findNavController().navigate(R.id.adminAchievementsFragment)
        }
        binding.btnSendNotification.setOnClickListener {
            showSendNotificationDialog()
        }
    }

    private fun showSendNotificationDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 0)
        }

        val etTitle = EditText(requireContext()).apply {
            hint = "Заглавие"
            setSingleLine()
        }
        val etMessage = EditText(requireContext()).apply {
            hint = "Съобщение"
            minLines = 2
        }

        layout.addView(etTitle)
        layout.addView(etMessage)

        AlertDialog.Builder(requireContext())
            .setTitle("Изпрати известие до всички")
            .setView(layout)
            .setPositiveButton("Изпрати") { _, _ ->
                val title = etTitle.text.toString().trim()
                val message = etMessage.text.toString().trim()
                if (title.isNotEmpty() && message.isNotEmpty()) {
                    sendNotification(title, message)
                } else {
                    Toast.makeText(requireContext(), "Попълнете всички полета", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отказ", null)
            .show()
    }

    private fun sendNotification(title: String, message: String) {
        lifecycleScope.launch {
            adminRepository.broadcastNotification(SendNotificationRequest(title, message))
                .onSuccess { count ->
                    Toast.makeText(requireContext(), "Известието е изпратено до $count потребители", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    Toast.makeText(requireContext(), it.message ?: "Грешка", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun observeViewModel() {
        viewModel.statsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminPanelViewModel.StatsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AdminPanelViewModel.StatsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val stats = state.stats
                    binding.tvTotalUsers.text = stats.totalUsers.toString()
                    binding.tvTotalPois.text = stats.totalPois.toString()
                    binding.tvTotalVisits.text = stats.totalVisits.toString()
                    binding.tvTotalMissions.text = stats.totalMissions.toString()
                    binding.tvTotalAchievements.text = stats.totalAchievements.toString()
                    binding.tvActiveUsers.text = stats.activeUsers.toString()
                }
                is AdminPanelViewModel.StatsState.Error -> {
                    binding.progressBar.visibility = View.GONE
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
