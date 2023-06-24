package com.rokoblak.gittrendingcompose.domain.usecases

import com.rokoblak.gittrendingcompose.service.AppStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DarkModeHandlingUseCase {
    fun darkModeEnabled(): Flow<Boolean?>

    suspend fun updateDarkMode(enabled: Boolean)
}

class AppDarkModeHandlingUseCase @Inject constructor(
    private val storage: AppStorage,
) : DarkModeHandlingUseCase {
    override fun darkModeEnabled(): Flow<Boolean?> = storage.prefsFlow().map { it.darkMode }

    override suspend fun updateDarkMode(enabled: Boolean) {
        storage.updateDarkMode(enabled)
    }

}