package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import com.example.android.politicalpreparedness.utils.afterTextChanged
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class DetailFragment : Fragment(R.layout.fragment_representative) {
    private lateinit var representativeAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_PERMISSION_CODE = 100
        const val REQUEST_TURN_DEVICE_LOCATION_ON_CODE = 101
    }

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding get() = _binding!!
    lateinit var spinnerAdapter: ArrayAdapter<String>
    private val viewModel: RepresentativeViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRepresentativeBinding.bind(view)
        with(binding) {
            representativeAdapter = RepresentativeListAdapter()
            representativeList.layoutManager = LinearLayoutManager(requireContext())
            representativeList.adapter = representativeAdapter
            spinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.states)
            )
            state.adapter = spinnerAdapter
            state.onItemSelectedListener = object : AdapterViewBindingAdapter.OnItemSelected,
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val value = spinnerAdapter.getItem(position) ?: ""
                    viewModel.onStateChanged(value)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            viewModel.representativesLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is AsyncTaskState.ErrorState -> {
                    }
                    AsyncTaskState.InitialState -> {
                    }
                    AsyncTaskState.LoadingState -> {
                    }
                    is AsyncTaskState.SuccessState -> {
                        representativeAdapter.submitList(state.data)
                    }
                }

            }
            viewModel.addressLiveData.observe(viewLifecycleOwner) { address ->
                if (address.line1 != addressLine1.text.toString()) {
                    addressLine1.setText(address.line1)
                }
                if (address.line2 != addressLine2.text.toString()) {
                    addressLine2.setText(address.line2)
                }
                if (address.city != city.text.toString()) {
                    city.setText(address.city)
                }
                if (state.selectedItemPosition == -1 || address.state != spinnerAdapter.getItem(
                        state.selectedItemPosition
                    )
                ) {
                    state.setNewValue(address.state)
                }

                if (address.zip != zip.text.toString()) {
                    zip.setText(address.zip)
                }
            }
            buttonSearch.setOnClickListener {
                viewModel.onFindMyRepresentativesClicked()
            }
            addressLine1.afterTextChanged {
                viewModel.onAddressLine1DataChanged(it)
            }
            addressLine2.afterTextChanged {
                viewModel.onAddressLine2DataChanged(it)
            }
            city.afterTextChanged {
                viewModel.onCityDataChanged(it)
            }
            zip.afterTextChanged {
                viewModel.onZipChanged(it)
            }

            buttonLocation.setOnClickListener {
                checkLocationPermissions()
            }
        }
    }

//        //TODO: Establish bindings
//
//        //TODO: Define and assign Representative adapter
//
//        //TODO: Populate Representative adapter
//
//        //TODO: Establish button listeners for field and location search

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isResultEmpty = grantResults.isEmpty()
        val isLocationExplicitlyDenied =
            grantResults[0] == PackageManager.PERMISSION_DENIED
        val isPermissionDenied = isResultEmpty || isLocationExplicitlyDenied

        if (isPermissionDenied) {
            onPermissionDenied()
        } else {
            onPermissionGranted()
        }
    }

    private fun onPermissionDenied() {
        Snackbar.make(
            requireView(),
            "Location permissions denied. Please enable for best experience.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Settings") {
            startActivity(Intent().apply {
                action = ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }.show()
    }

    private fun onPermissionGranted() {
        checkLocationSettings()
    }


    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            checkLocationSettings()
        } else {
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                onResolvableException(exception)
            } else {
                onNonResolvableException()
            }
        }
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        val geoCoder = Geocoder(context, Locale.getDefault())
                        if (location != null) {
                            val geoResults =
                                geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                                    .firstOrNull()
                            if (geoResults != null) {
                                viewModel.onLocationFound(geoResults)
                            }
                        }
                    }
            } else {
                // Do nothing at this time.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON_CODE && resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                delay(3_000) //this was required, since location would not be ready instantly
                checkLocationSettings()
            }
        }
    }

    private fun onResolvableException(exception: ResolvableApiException) {
        try {
            startIntentSenderForResult(
                exception.resolution.intentSender,
                REQUEST_TURN_DEVICE_LOCATION_ON_CODE,
                null,
                0,
                0,
                0,
                null
            )
        } catch (sendEx: IntentSender.SendIntentException) {
            // Do nothing for now.
        }
    }

    private fun onNonResolvableException() {
        Snackbar.make(
            requireView(),
            "Not able to enable location. Try again",
            Snackbar.LENGTH_INDEFINITE
        ).setAction(android.R.string.ok) {
            checkLocationSettings()
        }.show()
    }


    private fun isPermissionGranted(): Boolean {
        val selfPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return PackageManager.PERMISSION_GRANTED == selfPermission
    }

    private fun requestLocationPermissions() {
        if (isPermissionGranted()) {
            return
        }

        var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissions, REQUEST_PERMISSION_CODE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //TODO: Get location from LocationServices
    //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address

}