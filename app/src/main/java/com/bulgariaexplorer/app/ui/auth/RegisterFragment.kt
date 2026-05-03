package com.bulgariaexplorer.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.data.local.TokenManager
import com.bulgariaexplorer.app.data.repository.AuthRepository
import com.bulgariaexplorer.app.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(TokenManager(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                username.isEmpty() -> {
                    binding.tilUsername.error = getString(R.string.register_username_required)
                }
                email.isEmpty() -> {
                    binding.tilEmail.error = getString(R.string.register_email_required)
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = getString(R.string.register_password_required)
                }
                password.length < 8 -> {
                    binding.tilPassword.error = getString(R.string.register_password_min_length)
                }
                else -> {
                    binding.tilUsername.error = null
                    binding.tilEmail.error = null
                    binding.tilPassword.error = null
                    viewModel.register(username, email, password)
                }
            }
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                    // Navigate to home screen (will implement in Phase 2)
                    // For now, just navigate back to login
                    findNavController().popBackStack()
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.error_format, state.message), Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
