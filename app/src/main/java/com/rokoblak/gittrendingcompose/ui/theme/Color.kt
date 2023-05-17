package com.rokoblak.gittrendingcompose.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

val PrimaryDark = Color.DarkGray
val PrimaryLight = Color(0xFFD6D6EC)

val SecondaryLight = Color.DarkGray
val SecondaryDark = Color.LightGray

val Gold = Color(0xFFFFD700)
val ButtonGreen = Color(0xFF95C99A)

fun Color.alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Color = this.copy(alpha = alpha)