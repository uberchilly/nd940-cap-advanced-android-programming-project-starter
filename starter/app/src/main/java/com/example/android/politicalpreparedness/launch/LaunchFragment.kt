package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment(R.layout.fragment_launch) {
    private var _binding: FragmentLaunchBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLaunchBinding.bind(view)

        with(binding) {
            upcomingElectionBtn.setOnClickListener {
                navToElections()
            }
            findMyRepresentativesBtn.setOnClickListener {
                navToRepresentatives()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navToElections() {
        findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }

}
