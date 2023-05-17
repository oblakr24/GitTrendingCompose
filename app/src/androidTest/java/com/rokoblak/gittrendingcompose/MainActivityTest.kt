package com.rokoblak.gittrendingcompose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.TAG_DRAWER
import com.rokoblak.gittrendingcompose.ui.reposlisting.composables.TAG_NAV_BUTTON
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Very simple UI test that just clicks on the hamburger menu icon and asserts that the menu opens.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testDrawerOpens() {
        composeTestRule.onNodeWithTag(TAG_NAV_BUTTON).let {
            it.assertIsDisplayed()
            it.performClick()
        }

        composeTestRule.onNodeWithTag(TAG_DRAWER).assertIsDisplayed()
    }
}