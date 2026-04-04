package com.ehealth.dermassist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ehealth.dermassist.ui.theme.dimens

enum class ButtonVariant {
    Primary,
    Secondary,
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
) {
    val containerColor =
        if (variant == ButtonVariant.Primary) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.primary
    val contentColor =
        if (variant == ButtonVariant.Primary) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary
    val border =
        if (variant == ButtonVariant.Primary) {
            BorderStroke(MaterialTheme.dimens.borderThick, MaterialTheme.colorScheme.outlineVariant)
        } else {
            null
        }

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(MaterialTheme.dimens.buttonHeight),
        shape = RoundedCornerShape(MaterialTheme.dimens.radiusHuge),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        border = border,
        enabled = enabled,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
