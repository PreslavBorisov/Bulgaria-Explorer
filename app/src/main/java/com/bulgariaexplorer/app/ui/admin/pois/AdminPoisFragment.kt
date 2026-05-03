package com.bulgariaexplorer.app.ui.admin.pois

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
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.databinding.FragmentAdminPoisBinding
import com.bulgariaexplorer.app.utils.NavArgs

class AdminPoisFragment : Fragment() {

    private var _binding: FragmentAdminPoisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminPoisViewModel by viewModels {
        AdminPoisViewModelFactory(AdminRepository())
    }

    private lateinit var adapter: AdminPoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPoisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.loadPois()
    }

    private fun setupRecyclerView() {
        adapter = AdminPoiAdapter(emptyList()) { poi ->
            showPoiActionDialog(poi)
        }
        binding.rvPois.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPois.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddPoi.setOnClickListener {
            findNavController().navigate(R.id.poiFormFragment)
        }
    }

    private fun showPoiActionDialog(poi: PoiResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(poi.title)
            .setItems(arrayOf(getString(R.string.action_edit), getString(R.string.admin_poi_delete_option))) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply { putLong(NavArgs.POI_ID, poi.id) }
                        findNavController().navigate(R.id.poiFormFragment, bundle)
                    }
                    1 -> confirmDelete(poi)
                }
            }
            .show()
    }

    private fun confirmDelete(poi: PoiResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.admin_poi_delete_confirm, poi.title ?: ""))
            .setPositiveButton(getString(R.string.dialog_delete_confirm)) { _, _ -> viewModel.deletePoi(poi.id) }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.poisState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminPoisViewModel.PoisState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is AdminPoisViewModel.PoisState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updatePois(state.pois)
                    binding.tvEmpty.visibility = if (state.pois.isEmpty()) View.VISIBLE else View.GONE
                }
                is AdminPoisViewModel.PoisState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminPoisViewModel.ActionState.Success ->
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                is AdminPoisViewModel.ActionState.Error ->
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
