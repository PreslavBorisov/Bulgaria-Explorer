package com.bulgariaexplorer.app.ui.admin.pois

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bulgariaexplorer.app.data.remote.dto.CategoryResponse
import com.bulgariaexplorer.app.data.remote.dto.admin.AdminPoiRequest
import com.bulgariaexplorer.app.data.repository.AdminRepository
import com.bulgariaexplorer.app.R
import com.bulgariaexplorer.app.databinding.FragmentPoiFormBinding
import com.bulgariaexplorer.app.utils.NavArgs

class PoiFormFragment : Fragment() {

    private var _binding: FragmentPoiFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminPoisViewModel by viewModels {
        AdminPoisViewModelFactory(AdminRepository())
    }

    private var editPoiId: Long? = null
    private var categories: List<CategoryResponse> = emptyList()
    private var selectedCategoryId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoiFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editPoiId = arguments?.getLong(NavArgs.POI_ID)?.takeIf { it > 0 }

        setupListeners()
        observeViewModel()
        viewModel.loadCategories()

        if (editPoiId != null) {
            binding.tvFormTitle.text = getString(R.string.poi_form_title_edit)
            loadExistingPoi()
        }
    }

    private fun setupListeners() {
        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryId = categories[position].id
        }

        binding.btnSave.setOnClickListener {
            savePoi()
        }
    }

    private fun loadExistingPoi() {
        viewModel.loadPois()
        viewModel.poisState.observe(viewLifecycleOwner) { state ->
            if (state is AdminPoisViewModel.PoisState.Success) {
                val poi = state.pois.find { it.id == editPoiId }
                poi?.let {
                    binding.etTitle.setText(it.title ?: "")
                    binding.etShortDescription.setText(it.shortDescription ?: "")
                    binding.etFullDescription.setText(it.fullDescription ?: "")
                    binding.etLatitude.setText(it.latitude.toString())
                    binding.etLongitude.setText(it.longitude.toString())
                    binding.etAddress.setText(it.address ?: "")
                    binding.etCity.setText(it.city ?: "")
                    binding.etRegion.setText(it.region ?: "")
                    binding.etRewardPoints.setText(it.rewardPoints.toString())
                    binding.etOpeningHours.setText(it.openingHours ?: "")
                    binding.etSourceUrl.setText(it.sourceUrl ?: "")
                    binding.switchActive.isChecked = it.active

                    // Set category dropdown from name
                    it.categoryName?.let { name ->
                        binding.actvCategory.setText(name, false)
                        val cat = categories.find { c -> c.name == name }
                        selectedCategoryId = cat?.id
                    }
                }
            }
        }
    }

    private fun setupCategoryDropdown(cats: List<CategoryResponse>) {
        categories = cats
        val names = cats.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
        binding.actvCategory.setAdapter(adapter)

        // If editing, try to match category
        if (editPoiId != null && selectedCategoryId == null) {
            val currentText = binding.actvCategory.text.toString()
            if (currentText.isNotEmpty()) {
                val cat = cats.find { it.name == currentText }
                selectedCategoryId = cat?.id
            }
        }
    }

    private fun savePoi() {
        val title = binding.etTitle.text.toString().trim()
        val shortDesc = binding.etShortDescription.text.toString().trim().ifEmpty { null }
        val fullDesc = binding.etFullDescription.text.toString().trim().ifEmpty { null }
        val latStr = binding.etLatitude.text.toString().trim()
        val lonStr = binding.etLongitude.text.toString().trim()
        val address = binding.etAddress.text.toString().trim().ifEmpty { null }
        val city = binding.etCity.text.toString().trim().ifEmpty { null }
        val region = binding.etRegion.text.toString().trim().ifEmpty { null }
        val openingHours = binding.etOpeningHours.text.toString().trim().ifEmpty { null }
        val sourceUrl = binding.etSourceUrl.text.toString().trim().ifEmpty { null }
        val rewardPoints = binding.etRewardPoints.text.toString().toIntOrNull()
        val active = binding.switchActive.isChecked

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.field_title_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedCategoryId == null) {
            Toast.makeText(requireContext(), getString(R.string.poi_form_category_required), Toast.LENGTH_SHORT).show()
            return
        }
        val lat = latStr.toDoubleOrNull()
        val lon = lonStr.toDoubleOrNull()
        if (lat == null || lon == null) {
            Toast.makeText(requireContext(), getString(R.string.poi_form_coordinates_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        val request = AdminPoiRequest(
            title = title,
            shortDescription = shortDesc,
            fullDescription = fullDesc,
            latitude = lat,
            longitude = lon,
            categoryId = selectedCategoryId!!,
            address = address,
            city = city,
            region = region,
            openingHours = openingHours,
            sourceUrl = sourceUrl,
            rewardPoints = rewardPoints,
            active = active
        )

        if (editPoiId != null) {
            viewModel.updatePoi(editPoiId!!, request)
        } else {
            viewModel.createPoi(request)
        }
    }

    private fun observeViewModel() {
        viewModel.categoriesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminPoisViewModel.CategoriesState.Success -> {
                    setupCategoryDropdown(state.categories)
                }
                is AdminPoisViewModel.CategoriesState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AdminPoisViewModel.FormState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }
                is AdminPoisViewModel.FormState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is AdminPoisViewModel.FormState.Error -> {
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
