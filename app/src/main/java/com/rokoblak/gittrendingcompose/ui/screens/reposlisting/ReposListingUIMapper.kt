package com.rokoblak.gittrendingcompose.ui.screens.reposlisting


import androidx.compose.ui.graphics.Color
import com.rokoblak.gittrendingcompose.data.domain.GitRepository
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.RepoDisplayData

object ReposListingUIMapper {

    fun GitRepository.toDisplay() = RepoDisplayData(
        id = id.toString(),
        name = name,
        desc = desc,
        authorImgUrl = authorImgUrl,
        authorName = authorName,
        lang = lang,
        stars = stars.toString(),
        showsLang = lang != null,
        langColor = lang?.mapLangToColor(),
    )

    private fun String.mapLangToColor() = colorMap[lowercase()]?.let {
        Color(android.graphics.Color.parseColor(it))
    } ?: Color.Gray

    // API Does not return this, so this is just a quick mapping
    private val colorMap = mapOf(
        "javascript" to "#F1E05A",
        "python" to "#3572A5",
        "java" to "#B07219",
        "ruby" to "#701516",
        "php" to "#4F5D95",
        "c++" to "#F34B7D",
        "c#" to "#178600",
        "typescript" to "#2B7489",
        "shell" to "#89e051",
        "c" to "#555555",
        "swift" to "#FFAC45",
        "go" to "#00ADD8",
        "kotlin" to "#F18E33",
        "rust" to "#DEA584",
        "scala" to "#C22D40"
    )
}