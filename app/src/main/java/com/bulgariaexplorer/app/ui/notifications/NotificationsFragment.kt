package com.bulgariaexplorer.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.data.repository.NotificationRepository
import com.bulgariaexplorer.app.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationsViewModel by viewModels {
        NotificationsViewModelFactory(NotificationRepository())
    }

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadNotifications()
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(emptyList()) { notification ->
            if (!notification.read) {
                viewModel.markAsRead(notification.id)
            }
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnMarkAllRead.setOnClickListener {
            viewModel.markAllAsRead()
        }
    }

    private fun observeViewModel() {
        viewModel.notificationsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NotificationsViewModel.NotificationsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is NotificationsViewModel.NotificationsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateNotifications(state.notifications)
                    binding.tvEmpty.visibility = if (state.notifications.isEmpty()) View.VISIBLE else View.GONE
                }
                is NotificationsViewModel.NotificationsState.Error -> {
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
