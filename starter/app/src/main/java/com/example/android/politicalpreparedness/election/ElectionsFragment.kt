package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
class ElectionsFragment : Fragment(R.layout.fragment_election) {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    //TODO: Declare ViewModel

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        //TODO: Add ViewModel values and create ViewModel
//
//        //TODO: Add binding values
//
//        //TODO: Link elections to voter info
//
//        //TODO: Initiate recycler adapters
//
//        //TODO: Populate recycler adapters
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentElectionBinding.bind(view)

        with(binding){


        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}