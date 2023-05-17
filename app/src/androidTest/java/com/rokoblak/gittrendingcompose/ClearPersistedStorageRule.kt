package com.rokoblak.gittrendingcompose

import androidx.test.platform.app.InstrumentationRegistry
import com.rokoblak.gittrendingcompose.data.db.ReposDatabase
import com.rokoblak.gittrendingcompose.service.PersistedStorage
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Retrieves the dependencies from Hilt to manually clear them.
 * This is mostly to make sure the DataStore properly clears,
 * since otherwise we'd have to rely on manually deleting the files or obtaining a direct singleton instance otherwise.
 */
class ClearPersistedStorageRule : TestWatcher() {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface DependencyProvider {
        fun persistedStorage(): PersistedStorage
    }

    override fun finished(description: Description) {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        targetContext.deleteDatabase(ReposDatabase.NAME)
        val app = targetContext.applicationContext
        val provider = EntryPoints.get(app, DependencyProvider::class.java)

        runBlocking {
            provider.persistedStorage().clear()
        }
    }
}
