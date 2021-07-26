package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import kotlinx.coroutines.launch

interface VoterInfoRepository {
    suspend fun getVoterInfo(electionId: Int): VoterInfoResponse
}

class VoterInfoRepositoryImpl constructor(private val civicsApiService: CivicsApiService) :
    VoterInfoRepository {
    override suspend fun getVoterInfo(electionId: Int): VoterInfoResponse {
        return civicsApiService.getVoterInfo(
            electionId,
            "1600 Amphitheatre Parkway Mountain View, CA 94043"
        )
    }

}

class VoterInfoViewModel(
    savedStateHandle: SavedStateHandle,
    private val electionRepository: ElectionRepository,
    private val voterInfoRepository: VoterInfoRepository
) : ViewModel() {


    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    private val election: Election = savedStateHandle.get<Election>("arg_election")!!
    val isElectionFollowed =
        electionRepository.getFollowedElection()
            .asLiveData(viewModelScope.coroutineContext)
            .map { elections ->
                if (elections.isNullOrEmpty())
                    return@map false

                val foundElection: Election? = elections.find {
                    it.id == election.id
                }
                return@map foundElection != null
            }

    private val _voterInfoLoadState =
        MutableLiveData<AsyncTaskState<VoterInfoResponse, Exception>>()
    val voterInfoLoadState: LiveData<AsyncTaskState<VoterInfoResponse, Exception>>
        get() = _voterInfoLoadState

    init {
        _voterInfoLoadState.value = AsyncTaskState.LoadingState
        viewModelScope.launch {
            try {
                val result = voterInfoRepository.getVoterInfo(electionId = election.id)
                _voterInfoLoadState.value = AsyncTaskState.SuccessState(result)
            } catch (e: Exception) {
                _voterInfoLoadState.value = AsyncTaskState.ErrorState(e)
            }
        }
    }

    fun onFollowBtnClicked() {
        if (isElectionFollowed.value == true)
            unfollow()
        else
            follow()

    }

    private fun follow() = viewModelScope.launch {
        electionRepository.followElection(election)
    }

    private fun unfollow() = viewModelScope.launch {
        electionRepository.unfollowElection(election)
    }
}