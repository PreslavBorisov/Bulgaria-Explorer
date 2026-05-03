package com.bulgariaexplorer.app.ui.admin.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminUserResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.databinding.FragmentAdminUsersBinding

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminUsersViewModel by viewModels {
        AdminUsersViewModelFactory(AdminRepository())
    }

    private lateinit var adapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(emptyList()) { user ->
            showUserActionDialog(user)
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
    }

    private fun showUserActionDialog(user: AdminUserResponse) {
        val newRole = if (user.role == "ADMIN") "USER" else "ADMIN"
        val options = arrayOf(
            "Промени роля на $newRole",
            "Изтрий потребител"
        )
        AlertDialog.Builder(requireContext())
            .setTitle(user.username)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.changeUserRole(user.id, newRole)
                    1 -> confirmDelete(user)
                }
            }
            .show()
    }

    private fun confirmDelete(user: AdminUserResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Изтриване")
            .setMessage("Сигурни ли сте, че искате да изтриете ${user.username}?")
            .setPositiveButton("Изтрий") { _, _ -> viewModel.deleteUser(user.id) }
            .setNegativeButton("Отказ", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.usersState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminUsersViewModel.UsersState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is AdminUsersViewModel.UsersState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateUsers(state.users)
                    binding.tvEmpty.visibility = if (state.users.isEmpty()) View.VISIBLE else View.GONE
                }
                is AdminUsersViewModel.UsersState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminUsersViewModel.ActionState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is AdminUsersViewModel.ActionState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
