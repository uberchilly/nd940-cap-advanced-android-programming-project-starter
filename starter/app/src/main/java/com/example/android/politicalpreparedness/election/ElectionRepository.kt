package com.example.android.politicalpreparedness.election

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.flow.Flow

interface ElectionRepository {
    suspend fun getFreshElections(): List<Election>
    fun getFollowedElection(): Flow<List<Election>>
    suspend fun followElection(election: Election)
    suspend fun unfollowElection(election: Election)

    suspend fun getRepresentatives(address: Address): List<Representative>
}

class ElectionRepositoryImpl constructor(
    private val apiService: CivicsApiService,
    private val electionDao: ElectionDao
) :
    ElectionRepository {

    override suspend fun getFreshElections(): List<Election> {
        return apiService.getElections().elections
    }

    override fun getFollowedElection(): Flow<List<Election>> {
        return electionDao.getAllElectionsFlow()
    }

    override suspend fun followElection(election: Election) {
        electionDao.insertElection(election)
    }

    override suspend fun unfollowElection(election: Election) {
        electionDao.deleteElection(election)
    }

    override suspend fun getRepresentatives(address: Address): List<Representative> {
        val result: RepresentativeResponse =
            apiService.getRepresentativesByAddress(address = address.toFormattedString())
        return result.offices
            .flatMap {
                it.getRepresentatives(result.officials)
            }
    }

}