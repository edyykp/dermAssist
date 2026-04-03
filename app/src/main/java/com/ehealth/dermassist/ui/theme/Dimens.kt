package com.ehealth.dermassist.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    // Base scale
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 32.dp,
    val xl: Dp = 64.dp,
    val xxl: Dp = 128.dp,

    // Grid System
    val grid025: Dp = 2.dp,
    val grid05: Dp = 4.dp,
    val grid075: Dp = 6.dp,
    val grid1: Dp = 8.dp,
    val grid125: Dp = 10.dp,
    val grid15: Dp = 12.dp,
    val grid175: Dp = 14.dp,
    val grid2: Dp = 16.dp,
    val grid225: Dp = 18.dp,
    val grid25: Dp = 20.dp,
    val grid275: Dp = 22.dp,
    val grid3: Dp = 24.dp,
    val grid35: Dp = 28.dp,
    val grid4: Dp = 32.dp,
    val grid45: Dp = 36.dp,
    val grid5: Dp = 40.dp,
    val grid6: Dp = 48.dp,
    val grid7: Dp = 56.dp,
    val grid8: Dp = 64.dp,
    val grid9: Dp = 72.dp,

    // Corner Radii
    val radiusXs: Dp = 4.dp,
    val radiusSm: Dp = 8.dp,
    val radiusMd: Dp = 12.dp,
    val radiusLg: Dp = 16.dp,
    val radiusXl: Dp = 20.dp,
    val radiusXxl: Dp = 24.dp,
    val radiusHuge: Dp = 28.dp,

    // Icon Sizes
    val iconXs: Dp = 12.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 18.dp,
    val iconLg: Dp = 20.dp,
    val iconXl: Dp = 40.dp,

    // Component specific
    val buttonHeight: Dp = 56.dp,
    val avatarSize: Dp = 64.dp,
    val logoSize: Dp = 72.dp,
    val indicatorSize: Dp = 7.dp,

    // Borders & Elevations
    val borderThin: Dp = 0.5.dp,
    val borderNormal: Dp = 1.dp,
    val borderThick: Dp = 1.5.dp,
    val borderExtraThick: Dp = 3.dp,
    val elevationSm: Dp = 1.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
