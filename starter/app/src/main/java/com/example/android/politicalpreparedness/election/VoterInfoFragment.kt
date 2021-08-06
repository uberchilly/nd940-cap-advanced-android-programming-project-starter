package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.text.SimpleDateFormat


class VoterInfoFragment : Fragment(R.layout.fragment_voter_info) {
    private var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!

    private val simpleDateFormat = SimpleDateFormat("EEE MMM HH:mm:ss zzz YYYY")

    private val viewModel: VoterInfoViewModel by stateViewModel(state = {
        requireArguments()
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVoterInfoBinding.bind(view)


        with(binding) {
            followBtn.setOnClickListener {
                viewModel.onFollowBtnClicked()
            }
            viewModel.isElectionFollowed.observe(viewLifecycleOwner) { isElectionFollowed ->
                if (isElectionFollowed == true) {
                    followBtn.text = getString(R.string.unfollow)
                } else {
                    followBtn.text = getString(R.string.follow)
                }
            }
            viewModel.voterInfoLoadState.observe(viewLifecycleOwner) { voterInfoState ->
                when (voterInfoState) {
                    AsyncTaskState.InitialState -> {
                    }
                    AsyncTaskState.LoadingState -> {
                    }
                    is AsyncTaskState.ErrorState -> {
                    }
                    is AsyncTaskState.SuccessState -> {
                        updateVoterInfo(voterInfoState.data)
                    }
                }

            }
        }
    }

    private fun updateVoterInfo(data: VoterInfoResponse) {
        with(binding) {
            electionName.title = data.election.name
            electionDate.text = simpleDateFormat.format(data.election.electionDay)

            stateLocations.text = getString(R.string.voting_locations)
            val votingLocationUrl =
                data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
            if (votingLocationUrl != null) {
                stateLocations.visibility = View.VISIBLE
                stateLocations.setOnClickListener {
                    openUrl(
                        votingLocationUrl
                    )
                }
            } else {
                stateLocations.visibility = View.GONE
            }

            stateBallot.text = getString(R.string.ballot_info)
            val ballotInfoUrl = data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
            if (ballotInfoUrl != null) {
                stateBallot.visibility = View.VISIBLE
                stateBallot.setOnClickListener {
                    openUrl(
                        ballotInfoUrl
                    )
                }
            } else {
                stateBallot.visibility = View.GONE
            }


            val addressData = data.state?.get(0)?.electionAdministrationBody?.correspondenceAddress
            if (addressData != null) {
                addressGroup.visibility = View.VISIBLE
                stateCorrespondenceHeader.text = addressData.state
                address.text = addressData.toFormattedString()
            } else {
                addressGroup.visibility = View.GONE
            }

        }
    }

    private fun openUrl(url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}