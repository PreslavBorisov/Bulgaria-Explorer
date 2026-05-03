package com.bulgariaexplorer.app.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bulgariaexplorer.app.R
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bulgariaexplorer.app.data.repository.LeaderboardRepository
import com.bulgariaexplorer.app.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeaderboardViewModel by viewModels {
        LeaderboardViewModelFactory(LeaderboardRepository())
    }

    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = LeaderboardAdapter()
        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LeaderboardFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.leaderboardState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LeaderboardViewModel.LeaderboardState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is LeaderboardViewModel.LeaderboardState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.entries)
                }
                is LeaderboardViewModel.LeaderboardState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.myRankState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LeaderboardViewModel.MyRankState.Success -> {
                    binding.tvMyRank.text = "#${state.entry.rank ?: "-"}"
                    binding.tvMyPoints.text = getString(R.string.leaderboard_points_format, state.entry.totalPoints)
                    adapter.currentUserId = state.entry.userId
                }
                is LeaderboardViewModel.MyRankState.Error -> {
                    // Silently fail for my rank
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
