package com.rokoblak.gittrendingcompose.ui.reposlisting.composables

import androidx.compose.ui.graphics.Color

data class RepoDisplayData(
    val id: String,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: String,
    val showsLang: Boolean,
    val langColor: Color?,
)

// TODO: Composable