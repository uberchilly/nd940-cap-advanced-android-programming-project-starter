package com.example.android.politicalpreparedness.election

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.navigation.NavigationDispatcher
import com.example.android.politicalpreparedness.navigation.observe
import com.example.android.politicalpreparedness.utils.AsyncTaskState
import com.example.android.politicalpreparedness.utils.dpToPx
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ElectionsFragment : Fragment(R.layout.fragment_election) {
    private lateinit var upcomingElectionAdapter: ElectionListAdapter
    private lateinit var savedElectionsRecyclerAdapter: ElectionListAdapter
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private val navigationDispatcher: NavigationDispatcher by inject()

    private val viewModel: ElectionsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentElectionBinding.bind(view)
        with(binding) {
            navigationDispatcher.navigationCommands.observe(viewLifecycleOwner) {
                it.invoke(findNavController())
            }

            upcomingElectionAdapter = ElectionListAdapter {
                viewModel.onElectionItemClicked(it)
            }
            savedElectionsRecyclerAdapter = ElectionListAdapter {
                viewModel.onElectionItemClicked(it)
            }
            upcomingElectionsRecycler.adapter = upcomingElectionAdapter
            upcomingElectionsRecycler.layoutManager = LinearLayoutManager(requireContext())
            upcomingElectionsRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(
                    8.dpToPx()
                )
            )

            savedElectionsRecycler.adapter = savedElectionsRecyclerAdapter
            savedElectionsRecycler.layoutManager = LinearLayoutManager(requireContext())
            savedElectionsRecycler.addItemDecoration(
                VerticalSpaceItemDecoration(
                    8.dpToPx()
                )
            )

            viewModel.upcomingElectionsLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    AsyncTaskState.InitialState -> {
                    }
                    AsyncTaskState.LoadingState -> {
                        //add loading state
                    }
                    is AsyncTaskState.ErrorState -> {
                        //display error and retry
                    }
                    is AsyncTaskState.SuccessState -> {
                        upcomingElectionAdapter.updateList(it.data)
                    }
                }
            }

            viewModel.savedElectionLiveData.observe(viewLifecycleOwner) {
                it?.let { elections ->
                    savedElectionsRecyclerAdapter.updateList(elections)
                }
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = verticalSpaceHeight
    }
}