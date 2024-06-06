package com.rokoblak.gittrendingcompose.ui.common

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "dark theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    name = "light theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_NO
)
annotation class AppThemePreviews