package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.navigation.NavigationDispatcher
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel constructor(
    private val electionRepository: ElectionRepository,
    private val navigationDispatcher: NavigationDispatcher
) :
    ViewModel() {

    private val _upcomingElectionsLiveData =
        MutableLiveData<AsyncTaskState<List<Election>, Exception>>()
    val upcomingElectionsLiveData: LiveData<AsyncTaskState<List<Election>, Exception>>
        get() = _upcomingElectionsLiveData

    val savedElectionLiveData: LiveData<List<Election>> =
        electionRepository.getFollowedElection().asLiveData(viewModelScope.coroutineContext)

    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    init {
        loadElections()
    }

    private fun loadElections() {
        _upcomingElectionsLiveData.value = AsyncTaskState.LoadingState
        viewModelScope.launch {
            try {
                val electionList: List<Election> = electionRepository.getFreshElections()
                Timber.d("elecctions: ${electionList.size}")
                _upcomingElectionsLiveData.value = AsyncTaskState.SuccessState(electionList)
            } catch (e: Exception) {
                Timber.e(e)
                _upcomingElectionsLiveData.value = AsyncTaskState.ErrorState(e)
            }
        }
    }

    fun onElectionItemClicked(election: Election) {
        navigationDispatcher.emit { navController ->
            navController.navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election
                )
            )
        }
    }
}