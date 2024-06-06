package com.rokoblak.gittrendingcompose.ui.screens.repodetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.PreviewDataUtils
import com.rokoblak.gittrendingcompose.ui.common.composables.AvatarImage
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme
import com.rokoblak.gittrendingcompose.ui.theme.Gold

data class RepoHeaderCellData(
    val authorImgUrl: String?,
    val authorName: String,
    val title: String,
    val subtitle: String,
    val stars: String,
    val forks: String,
    val issues: String,
    val mainBranch: String,
)

@Composable
fun RepoHeaderCell(data: RepoHeaderCellData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(data.authorImgUrl)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = data.authorName, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = data.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Filled.Star,
                    tint = Gold,
                    contentDescription = "Stars count"
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = data.stars,
                    style = MaterialTheme.typography.labelMedium,
                )
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Filled.CallSplit,
                    tint = Gold,
                    contentDescription = "Forks count"
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = data.forks,
                    style = MaterialTheme.typography.labelMedium,
                )
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Filled.ErrorOutline,
                    tint = Gold,
                    contentDescription = "Issues count"
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = data.issues,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = data.mainBranch, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .heightIn(0.dp, 200.dp)
                    .verticalScroll(rememberScrollState()),
                text = data.subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
@AppThemePreviews
fun RepoHeaderCellPreview() {
    GitTrendingComposeTheme {
        RepoHeaderCell(PreviewDataUtils.repoHeaderData)
    }
}
