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
import com.bulgariaexplorer.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(TokenManager(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.tilEmail.error = getString(R.string.login_email_required)
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = getString(R.string.login_password_required)
                }
                else -> {
                    binding.tilEmail.error = null
                    binding.tilPassword.error = null
                    viewModel.login(email, password)
                }
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show()
                    // Navigate to home screen
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.error_format, state.message), Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
