package com.ehealth.dermassist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.ehealth.dermassist.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmExitDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    title: String,
    message: String,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = true,
            ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.dimens.md).wrapContentWidth()) {

                // Title
                Text(text = title, style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.sm))

                // Message
                Text(text = message, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.lg))

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("Confirm") }
                }
            }
        }
    }
}
