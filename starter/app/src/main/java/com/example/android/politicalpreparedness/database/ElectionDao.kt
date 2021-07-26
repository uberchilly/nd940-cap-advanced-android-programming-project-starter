package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert
    suspend fun insertElection(election: Election)

    @Insert
    suspend fun insertElections(elections: List<Election>)

    //TODO: Add select all election query
    @Query("select * from election_table")
    suspend fun getAllElections(): List<Election>

    @Query("select * from election_table")
    fun getAllElectionsFlow(): Flow<List<Election>>

    //TODO: Add select single election query

    //TODO: Add delete query
    @Delete
    suspend fun deleteElection(election: Election)

    //TODO: Add clear query
    @Query("delete from election_table")
    suspend fun clearAll()

    @Query("select * from election_table where id = :id")
    fun getElectionByIdFlow(id: Int): Flow<Election?>

}