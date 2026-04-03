package com.ehealth.dermassist.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 32.dp,
    val xl: Dp = 64.dp,
    val xxl: Dp = 128.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
