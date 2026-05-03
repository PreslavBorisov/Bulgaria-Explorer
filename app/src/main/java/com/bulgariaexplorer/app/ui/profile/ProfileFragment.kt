package com.bulgariaexplorer.app.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.local.TokenManager
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
import com.bulgariaexplorer.app.data.repository.UserRepository
import com.bulgariaexplorer.app.databinding.FragmentProfileBinding
import com.bulgariaexplorer.app.utils.PrefKeys
import kotlinx.coroutines.launch
import androidx.core.content.edit

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            UserRepository(),
            LeaderboardRepository()
        )
    }

    private lateinit var achievementAdapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        loadThemePreference()
    }

    private fun setupRecyclerView() {
        achievementAdapter = AchievementAdapter()
        binding.rvAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = achievementAdapter
        }
    }

    private fun setupListeners() {
        binding.btnMissions.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_missionsFragment)
        }

        binding.btnMyVisits.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myVisitsFragment)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_favoritesFragment)
        }

        binding.btnAdminPanel.setOnClickListener {
            findNavController().navigate(R.id.adminPanelFragment)
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            saveThemePreference(isChecked)
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                val tokenManager = TokenManager(requireContext())
                tokenManager.clearToken()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    private fun loadThemePreference() {
        val prefs = requireContext().getSharedPreferences(PrefKeys.PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(PrefKeys.DARK_MODE, false)
        binding.switchDarkMode.isChecked = isDarkMode
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val prefs = requireContext().getSharedPreferences(PrefKeys.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(PrefKeys.DARK_MODE, isDarkMode) }
    }

    private fun observeViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileViewModel.UserState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ProfileViewModel.UserState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.apply {
                        tvUsername.text = state.user.username.uppercase()
                        tvPoints.text = state.user.totalPoints.toString()
                        tvLevel.text = state.user.level.toString()
                        tvStreak.text = state.user.streakDays.toString()
                        btnAdminPanel.visibility = if (state.user.role == "ADMIN") View.VISIBLE else View.GONE
                    }
                }
                is ProfileViewModel.UserState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.achievementsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileViewModel.AchievementsState.Loading -> {
                    // Already showing progress bar
                }
                is ProfileViewModel.AchievementsState.Success -> {
                    achievementAdapter.submitList(state.achievements)
                }
                is ProfileViewModel.AchievementsState.Error -> {
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
