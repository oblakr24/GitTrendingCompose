package com.rokoblak.gittrendingcompose.ui.common.composables

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AvatarImage(url: String?) {
    if (url != null) {
        Surface(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(36.dp),
            color = MaterialTheme.colorScheme.background,
            shape = CircleShape,
            shadowElevation = 2.dp,
        ) {
            AsyncImage(
                modifier = Modifier.padding(0.dp),
                model = Uri.parse(url),
                contentDescription = null
            )
        }
    } else {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .background(Color.LightGray, shape = CircleShape),
        )
    }
}
