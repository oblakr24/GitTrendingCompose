package com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.common.PreviewDataUtils
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme
import com.rokoblak.gittrendingcompose.ui.theme.Gold

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

@Composable
fun RepoDisplay(data: RepoDisplayData, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {

        if (data.authorImgUrl != null) {
            Surface(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp),
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape,
                shadowElevation = 2.dp,
            ) {
                AsyncImage(modifier = Modifier.padding(0.dp), model = Uri.parse(data.authorImgUrl), contentDescription = null)
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(36.dp)
                    .background(Color.LightGray, shape = CircleShape),
            )
        }

        Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
            Text(
                modifier = Modifier, text = data.authorName,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier,
                text = data.name,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier,
                text = data.desc ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (data.showsLang) {
                    Surface(
                        modifier = Modifier
                            .size(12.dp)
                            .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
                        color = data.langColor ?: Color.Gray,
                        shape = CircleShape,
                    ) {}
                    Text(
                        modifier = Modifier.padding(start = 4.dp, end = 12.dp),
                        text = data.lang ?: "",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
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
            }
        }
    }
}

@AppThemePreviews
@Composable
fun ContactDisplayPreview() {
    GitTrendingComposeTheme {
        RepoDisplay(data = PreviewDataUtils.repoData)
    }
}

@Preview
@Composable
fun ContactDisplayPreviewNoLanguage() {
    GitTrendingComposeTheme {
        RepoDisplay(data = PreviewDataUtils.repoData.copy(showsLang = false))
    }
}
