package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.election.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import kotlinx.coroutines.launch

class RepresentativeViewModel constructor(private val electionRepository: ElectionRepository) :
    ViewModel() {


    private val _addressLiveData = MutableLiveData<Address>()
    val addressLiveData: LiveData<Address>
        get() = _addressLiveData

    private val _representativesLiveData =
        MutableLiveData<AsyncTaskState<List<Representative>, Exception>>()
    val representativesLiveData: LiveData<AsyncTaskState<List<Representative>, Exception>>
        get() = _representativesLiveData

    fun onFindMyRepresentativesClicked() {
        findRepresentatives()
    }

    private fun getAddress(): Address {
        return if (_addressLiveData.value == null)
            Address("", "", "", "", "")
        else
            _addressLiveData.value!!
    }

    fun onAddressLine1DataChanged(newData: String) {
        val currentAddress = getAddress()
        if (newData != currentAddress.line1)
            _addressLiveData.value = currentAddress.copy(line1 = newData)
    }

    fun onLocationFound(geoResults: android.location.Address) {
        _addressLiveData.value = Address(
            line1 = geoResults.thoroughfare ?: "",
            line2 = geoResults.subThoroughfare ?: "",
            city = geoResults.locality ?: "",
            state = geoResults.adminArea ?: "",
            zip = geoResults.postalCode ?: ""
        )

        findRepresentatives()
    }

    private fun findRepresentatives() {
        val currentAddress = getAddress()
        _representativesLiveData.value = AsyncTaskState.LoadingState
        viewModelScope.launch {
            try {
                val result = electionRepository.getRepresentatives(currentAddress)
                _representativesLiveData.value = AsyncTaskState.SuccessState(result)
            } catch (e: Exception) {
                _representativesLiveData.value = AsyncTaskState.ErrorState(e)
            }
        }
    }

    fun onAddressLine2DataChanged(newData: String) {
        val currentAddress = getAddress()
        if (newData != currentAddress.line2)
            _addressLiveData.value = currentAddress.copy(line2 = newData)
    }

    fun onCityDataChanged(newData: String) {
        val currentAddress = getAddress()
        if (newData != currentAddress.city)
            _addressLiveData.value = currentAddress.copy(city = newData)
    }

    fun onZipChanged(newData: String) {
        val currentAddress = getAddress()
        if (newData != currentAddress.zip)
            _addressLiveData.value = currentAddress.copy(zip = newData)
    }

    fun onStateChanged(newData: String) {
        val currentAddress = getAddress()
        if (newData != currentAddress.state)
            _addressLiveData.value = currentAddress.copy(state = newData)
    }
}
