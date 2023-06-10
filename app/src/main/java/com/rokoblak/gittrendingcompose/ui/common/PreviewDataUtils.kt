package com.rokoblak.gittrendingcompose.ui.common

import androidx.compose.ui.graphics.Color
import com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables.RepoHeaderCellData
import com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables.RepoDisplayData

object PreviewDataUtils {

    val repoData = RepoDisplayData(
        id = "1",
        name = "RepoName",
        authorName = "Author Name",
        authorImgUrl = null,
        desc = "Long description that does not fit in just two lines so that we can make it ellipsize after we run out of space for testing, some more text to make it longer and longer still",
        lang = "Python",
        showsLang = true,
        stars = "115",
        langColor = Color.Blue,
    )

    val repoHeaderData = RepoHeaderCellData(
        authorImgUrl = null,
        authorName = "Author Name",
        title = "Repo Title",
        subtitle = "Repo Subtitle",
        stars = "234",
        forks = "32",
        issues = "2145",
        mainBranch = "main",
    )

    val randomRepos by lazy {
        (0..10).map { idx ->
            repoData.copy(id = idx.toString(), name = repoData.name + " $idx")
        }
    }
}