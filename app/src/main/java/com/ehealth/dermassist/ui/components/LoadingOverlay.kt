package com.ehealth.dermassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.3f),
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundColor)
                // Block all pointer input to underlying layers
                .pointerInput(Unit) {}
                // Consume clicks to prevent them from passing through
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
