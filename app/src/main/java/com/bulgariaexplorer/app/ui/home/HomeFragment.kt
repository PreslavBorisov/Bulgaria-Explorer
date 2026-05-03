package com.bulgariaexplorer.app.ui.home

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
import com.bulgariaexplorer.app.data.repository.NotificationRepository
import com.bulgariaexplorer.app.data.repository.PoiRepository
import com.bulgariaexplorer.app.data.repository.UserRepository
import com.bulgariaexplorer.app.databinding.FragmentHomeBinding
import com.bulgariaexplorer.app.utils.NavArgs
import com.bulgariaexplorer.app.ui.notifications.NotificationsViewModel
import com.bulgariaexplorer.app.ui.notifications.NotificationsViewModelFactory
import com.bulgariaexplorer.app.ui.poi.PoiAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            UserRepository(),
            PoiRepository()
        )
    }

    private val notificationsViewModel: NotificationsViewModel by viewModels {
        NotificationsViewModelFactory(NotificationRepository())
    }

    private lateinit var poiAdapter: PoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        notificationsViewModel.loadUnreadCount()
    }

    private fun setupRecyclerView() {
        poiAdapter = PoiAdapter { poi ->
            val action = R.id.action_homeFragment_to_poiDetailsFragment
            val bundle = Bundle().apply {
                putLong(NavArgs.POI_ID, poi.id)
            }
            findNavController().navigate(action, bundle)
        }

        binding.rvFeaturedPois.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = poiAdapter
        }
    }

    private fun setupListeners() {
        binding.btnNotifications.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeViewModel.UserState.Loading -> {
                    binding.progressBarUser.visibility = View.VISIBLE
                }
                is HomeViewModel.UserState.Success -> {
                    binding.progressBarUser.visibility = View.GONE
                    binding.apply {
                        tvUsername.text = state.user.username.uppercase()
                        tvPoints.text = state.user.totalPoints.toString()
                        tvLevel.text = state.user.level.toString()
                        tvStreak.text = state.user.streakDays.toString()
                    }
                }
                is HomeViewModel.UserState.Error -> {
                    binding.progressBarUser.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.featuredPoisState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeViewModel.PoiState.Loading -> {
                    binding.progressBarPois.visibility = View.VISIBLE
                }
                is HomeViewModel.PoiState.Success -> {
                    binding.progressBarPois.visibility = View.GONE
                    poiAdapter.submitList(state.pois)
                }
                is HomeViewModel.PoiState.Error -> {
                    binding.progressBarPois.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }

        notificationsViewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvNotificationBadge.visibility = View.VISIBLE
                binding.tvNotificationBadge.text = if (count > 9) "9+" else count.toString()
            } else {
                binding.tvNotificationBadge.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
