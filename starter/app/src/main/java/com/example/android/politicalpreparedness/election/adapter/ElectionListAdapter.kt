package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat

class ElectionListAdapter(private val clickListener: ElectionListener? = null) :
    RecyclerView.Adapter<ElectionListAdapter.ElectionViewHolder>() {
    private var elections: List<Election> = emptyList()

    fun updateList(newList: List<Election>) {

        val diffResult = DiffUtil.calculateDiff(ElectionDiffCallback(elections, newList))
        elections = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_election, parent, false)
        return ElectionViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(elections[position])
    }

    override fun getItemCount(): Int {
        return elections.size
    }

    class ElectionViewHolder(itemView: View, private val clickListener: ElectionListener?) :
        RecyclerView.ViewHolder(itemView) {
        private val electionTitle: TextView = itemView.findViewById(R.id.electionTitle)
        private val electionDate: TextView = itemView.findViewById(R.id.electionDate)
        private val simpleDateFormat = SimpleDateFormat("EEE MMM HH:mm:ss zzz YYYY")

        fun bind(election: Election) {
            itemView.setOnClickListener {
                clickListener?.onElectionClicked(election)
            }
            electionTitle.text = election.name
            electionDate.text = simpleDateFormat.format(election.electionDay)
        }
    }

    fun interface ElectionListener {
        fun onElectionClicked(election: Election)
    }
}


class ElectionDiffCallback(
    private val oldElections: List<Election>,
    private val newElections: List<Election>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldElections.size
    }

    override fun getNewListSize(): Int {
        return newElections.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldElections[oldItemPosition].id == newElections[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldElections[oldItemPosition] == newElections[newItemPosition]
    }

}

//TODO: Create ElectionViewHolder

//TODO: Create ElectionDiffCallback

//TODO: Create ElectionListener