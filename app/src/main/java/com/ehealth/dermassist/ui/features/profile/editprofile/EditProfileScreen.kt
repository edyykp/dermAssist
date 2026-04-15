package com.ehealth.dermassist.ui.features.profile.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.theme.*

// ─── Validation Helpers ───────────────────────────────────────────────────────

private fun validateName(name: String): String? =
    when {
        name.isBlank() -> "Name cannot be empty"
        name.length < 2 -> "Name must be at least 2 characters"
        name.length > 50 -> "Name must be under 50 characters"
        else -> null
    }

private fun validateAge(age: String): String? =
    when {
        age.isBlank() -> "Age cannot be empty"
        age.toIntOrNull() == null -> "Please enter a valid number"
        age.toInt() !in 1..120 -> "Please enter a valid age (1–120)"
        else -> null
    }

// ─── Edit Profile Screen ──────────────────────────────────────────────────────

@Composable
fun EditProfileScreen(
    profile: User? = null,
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: EditProfileScreenViewModel = hiltViewModel(),
) {
    val dimens = MaterialTheme.dimens
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf(profile?.name) }
    var age by remember { mutableStateOf((profile?.age ?: "").toString()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var isDirty by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    fun attemptSave() {
        nameError = validateName(name ?: "")
        ageError = validateAge(age)
        if (nameError == null && ageError == null) {
            viewModel.saveChanges(name?.trim() ?: "", age.trim().toInt(), onSave)
        }
    }

    // ── Root: scrollable content + fixed bottom bar ───────────────────────────
    Box(
        modifier =
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding()
    ) {

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    // Bottom padding so content is never hidden behind the button bar
                    .padding(
                        bottom = dimens.grid9 + dimens.grid9 + dimens.xl
                    ) // ~144dp — 2 buttons + spacing
        ) {
            Spacer(modifier = Modifier.height(dimens.md))

            EditProfileTopBar(onCancel = onCancel)
            AvatarSection(initials = profile?.getInitials() ?: "")
            EditableBanner()

            Spacer(modifier = Modifier.height(dimens.md))

            FieldSectionLabel(label = "Editable Fields")

            Spacer(modifier = Modifier.height(dimens.sm))

            EditableTextField(
                label = "FULL NAME",
                value = name ?: "",
                placeholder = "Enter your full name",
                errorText = nameError,
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions =
                    KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                onValueChange = {
                    name = it
                    isDirty = true
                    nameError = null
                },
            )

            Spacer(modifier = Modifier.height(dimens.md))

            EditableTextField(
                label = "AGE",
                value = age,
                placeholder = "Enter your age",
                errorText = ageError,
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            attemptSave()
                        }
                    ),
                onValueChange = {
                    if (it.length <= 3 && it.all { c -> c.isDigit() }) {
                        age = it
                        isDirty = true
                        ageError = null
                    }
                },
            )

            Spacer(modifier = Modifier.height(dimens.grid25))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimens.grid25),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                thickness = dimens.borderNormal,
            )

            Spacer(modifier = Modifier.height(dimens.grid25))

            FieldSectionLabel(label = "Read-only Fields")

            Spacer(modifier = Modifier.height(dimens.sm))

            ReadOnlyField(label = "MEMBER SINCE", value = profile?.memberSince ?: "")

            Spacer(modifier = Modifier.height(dimens.md))

            ReadOnlyField(
                label = "EMAIL",
                value = profile?.email ?: "",
                hint = "Linked to your sign-in account",
            )
        }

        // ── Fixed bottom button bar ───────────────────────────────────────────
        EditProfileBottomBar(
            isDirty = isDirty,
            onSave = { attemptSave() },
            onCancel = onCancel,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (isLoading) {
        LoadingOverlay()
    }
}

// ─── Bottom Bar ───────────────────────────────────────────────────────────────

@Composable
private fun EditProfileBottomBar(
    isDirty: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = MaterialTheme.dimens

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = MaterialTheme.dimens.md, // lifts the bar visually above scroll content
        tonalElevation = MaterialTheme.dimens.xxs,
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .navigationBarsPadding() // respects gesture bar / home indicator
                    .padding(
                        start = dimens.grid25,
                        end = dimens.grid25,
                        top = dimens.md,
                        bottom = dimens.md,
                    ),
            verticalArrangement = Arrangement.spacedBy(dimens.grid15),
        ) {
            // Save — primary
            Button(
                onClick = onSave,
                enabled = isDirty,
                modifier = Modifier.fillMaxWidth().height(dimens.buttonHeight),
                shape = RoundedCornerShape(dimens.radiusHuge),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor =
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null,
                    modifier = Modifier.size(dimens.iconSm),
                )
                Spacer(modifier = Modifier.width(dimens.sm))
                Text(text = "Save Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            // Cancel — outlined
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(dimens.buttonHeight),
                shape = RoundedCornerShape(dimens.radiusHuge),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
            ) {
                Text(text = "Cancel", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun EditProfileTopBar(onCancel: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.grid15),
    ) {
        // Back / close button
        IconButton(
            onClick = onCancel,
            modifier =
                Modifier.size(dimens.grid45)
                    .clip(RoundedCornerShape(dimens.radiusMd))
                    .background(MaterialTheme.colorScheme.background),
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(dimens.iconMd),
            )
        }
        Text(
            text = "Edit Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// ─── Avatar Section ───────────────────────────────────────────────────────────

@Composable
private fun AvatarSection(initials: String) {
    val dimens = MaterialTheme.dimens

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = dimens.grid35),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.grid15),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Avatar circle
            Box(
                modifier =
                    Modifier.size(dimens.grid9)
                        .clip(CircleShape)
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary,
                                        )
                                )
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

// ─── Info Banner ──────────────────────────────────────────────────────────────

@Composable
private fun EditableBanner() {
    val dimens = MaterialTheme.dimens

    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = dimens.grid25)
                .clip(RoundedCornerShape(dimens.radiusLg))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(dimens.md),
        horizontalArrangement = Arrangement.spacedBy(dimens.grid125),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier.size(dimens.grid4)
                    .clip(RoundedCornerShape(dimens.radiusSm))
                    .background(MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimens.iconMd),
            )
        }
        Column {
            Text(
                text = "What can you edit?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(dimens.xxs))
            Text(
                text =
                    "Only your name and age can be updated. Skin type and other details are set automatically by your scan history.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 18.sp,
            )
        }
    }
}

// ─── Section Label ────────────────────────────────────────────────────────────

@Composable
private fun FieldSectionLabel(label: String) {
    val dimens = MaterialTheme.dimens

    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = dimens.grid25),
    )
}

// ─── Editable Text Field ──────────────────────────────────────────────────────

@Composable
private fun EditableTextField(
    label: String,
    value: String,
    placeholder: String,
    errorText: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit,
) {
    val dimens = MaterialTheme.dimens
    val isError = errorText != null
    val tintColor =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.padding(horizontal = dimens.grid25)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = dimens.grid075),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit $label",
                    tint = tintColor,
                    modifier = Modifier.size(dimens.iconMd),
                )
            },
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            shape = RoundedCornerShape(dimens.radiusMd),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                ),
        )
        // Inline error message
        if (isError) {
            Spacer(modifier = Modifier.height(dimens.xs))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.xs),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(dimens.iconXs),
                )
                Text(
                    text = errorText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ─── Read-only Field ──────────────────────────────────────────────────────────

@Composable
private fun ReadOnlyField(label: String, value: String, hint: String? = null) {
    val dimens = MaterialTheme.dimens

    Column(modifier = Modifier.padding(horizontal = dimens.grid25)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = dimens.grid075),
        )
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(dimens.radiusMd))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = dimens.md, vertical = dimens.grid175),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Read-only",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(dimens.iconSm),
            )
        }
        if (hint != null) {
            Spacer(modifier = Modifier.height(dimens.xs))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.xs),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(dimens.iconXs),
                )
                Text(
                    text = hint,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileScreenPreview() {
    DermAssistTheme { EditProfileScreen() }
}
