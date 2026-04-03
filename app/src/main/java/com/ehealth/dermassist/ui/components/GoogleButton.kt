package com.ehealth.dermassist.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ehealth.dermassist.R
import com.ehealth.dermassist.ui.theme.dimens

@Composable
fun GoogleButton(onGoogleButtonClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onGoogleButtonClick,
        modifier = Modifier.fillMaxWidth().height(MaterialTheme.dimens.buttonHeight),
        shape = RoundedCornerShape(MaterialTheme.dimens.radiusHuge),
        border =
            androidx.compose.foundation.BorderStroke(
                MaterialTheme.dimens.borderThick,
                MaterialTheme.colorScheme.outline,
            ),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.variant_2),
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.iconLg),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.md))
            Text(
                text = "Continue with Google",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
