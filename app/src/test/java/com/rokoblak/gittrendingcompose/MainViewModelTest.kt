package com.rokoblak.gittrendingcompose

import com.rokoblak.gittrendingcompose.service.PersistedStorage
import com.rokoblak.gittrendingcompose.ui.main.MainScreenUIState
import com.rokoblak.gittrendingcompose.ui.main.MainViewModel
import com.rokoblak.gittrendingcompose.util.TestCoroutineRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @ExperimentalCoroutinesApi
    @Rule
    @JvmField
    val coroutineTestRule = TestCoroutineRule(unconfined = true)

    @Test
    fun testMappingWorksCorrectly() = coroutineTestRule.runTest {

        val storage = object : PersistedStorage {
            override fun prefsFlow(): Flow<PersistedStorage.Prefs> = flowOf(PersistedStorage.Prefs(darkMode = true))
            override suspend fun updateDarkMode(enabled: Boolean) = Unit
            override suspend fun clear() = Unit
        }

        val vm = MainViewModel(storage)

        val state = vm.uiState.first()

        assertEquals(MainScreenUIState(isDarkTheme = true), state)
    }
}
