package com.rokoblak.gittrendingcompose.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReposDao {

    @Query("SELECT * FROM gitrepoentity ORDER BY orderIdx")
    suspend fun getAll(): List<GitRepoEntity>

    @Query("SELECT * FROM gitrepoentity ORDER BY orderIdx")
    fun getAllFlow(): Flow<List<GitRepoEntity>>

    // Sometimes an api will return some data from a previous page for a next page load (in case there was some time between the loads) resulting in a conflict, so we need to just overwrite the old one
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<GitRepoEntity>)

    @Query("DELETE FROM gitrepoentity")
    suspend fun deleteAll()
}
