package com.rokoblak.gittrendingcompose.ui.screens.reposlisting.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rokoblak.gittrendingcompose.R
import com.rokoblak.gittrendingcompose.ui.common.AppThemePreviews
import com.rokoblak.gittrendingcompose.ui.theme.ButtonGreen
import com.rokoblak.gittrendingcompose.ui.theme.GitTrendingComposeTheme

@Composable
fun ErrorCell(
    isNoConnection: Boolean,
    modifier: Modifier = Modifier,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.retry))
        val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = if (isNoConnection) R.string.error_no_connection else R.string.error_generic),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = if (isNoConnection) R.string.error_no_connection_desc else R.string.error_generic_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(48.dp))
        // Could be extracted in a default composable if decided to use generically in the app
        Button(
            onClick = onRetryClicked,
            border = BorderStroke(1.dp, ButtonGreen),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.general_retry).uppercase(),
                color = ButtonGreen
            )
        }
    }
}

@AppThemePreviews
@Composable
fun ErrorDisplayPreview() {
    GitTrendingComposeTheme {
        ErrorCell(isNoConnection = false, onRetryClicked = {})
    }
}