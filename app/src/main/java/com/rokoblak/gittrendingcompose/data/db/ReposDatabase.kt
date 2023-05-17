package com.rokoblak.gittrendingcompose.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rokoblak.gittrendingcompose.data.db.model.GitRepoEntity


const val REPOS_DB_VERSION = 1

@Database(entities = [GitRepoEntity::class], version = REPOS_DB_VERSION, exportSchema = false)
abstract class ReposDatabase : RoomDatabase() {

    abstract fun reposDao(): ReposDao

    companion object {
        const val NAME = "DB_REPOS"
    }
}
