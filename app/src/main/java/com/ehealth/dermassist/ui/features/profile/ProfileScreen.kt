package com.ehealth.dermassist.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.ui.components.ConfirmExitDialog
import com.ehealth.dermassist.ui.theme.*

@Composable
fun ProfileScreen(
    user: User? = null,
    onEditProfileClick: () -> Unit,
    onPrivacyAndDataClick: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val totalScans by viewModel.totalScans.collectAsState()

    LaunchedEffect(user?.id) { user?.id?.let { viewModel.setUserId(it) } }

    if (showLogoutDialog) {
        ConfirmExitDialog(
            title = "Log Out",
            message = "Are you sure you want to log out of your account?",
            onConfirm = {
                viewModel.logout()
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false },
        )
    }

    if (showClearDataDialog) {
        ConfirmExitDialog(
            title = "Clear All Data",
            message =
                "This will permanently delete your user profile and all associated data from our servers. This action cannot be undone.",
            onConfirm = {
                viewModel.clearUserData {
                    // Success handling (e.g. show a snackbar or navigate)
                }
                showClearDataDialog = false
            },
            onDismiss = { showClearDataDialog = false },
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Content
        Column(
            modifier =
                Modifier.weight(1f)
                    .padding(horizontal = MaterialTheme.dimens.md)
                    .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.dimens.lg)) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                )
            }

            // Profile Card
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.dimens.radiusXxl))
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            Color(0xFFE4EEF9),
                                        )
                                )
                        )
                        .padding(MaterialTheme.dimens.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Avatar
                    Box(
                        modifier =
                            Modifier.size(MaterialTheme.dimens.avatarSize)
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
                                )
                                .border(
                                    MaterialTheme.dimens.borderExtraThick,
                                    MaterialTheme.colorScheme.onPrimary,
                                    CircleShape,
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = user?.getInitials()?.uppercase() ?: "",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.md))

                    // Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text =
                                "Age ${user?.age ?: "Unknown"} · Joined ${user?.memberSince ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                    }
                }

                // Edit Button
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).clickable { onEditProfileClick() },
                    shape = RoundedCornerShape(MaterialTheme.dimens.radiusMd),
                    color = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Text(
                        text = "Edit",
                        modifier =
                            Modifier.padding(
                                horizontal = MaterialTheme.dimens.md,
                                vertical = MaterialTheme.dimens.sm,
                            ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                    )
                }
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.dimens.md),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.sm),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = totalScans.toString(),
                    label = "Total Scans",
                )
            }

            // Account Section
            SectionCard(title = "Account") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    iconContainerColor = MaterialTheme.colorScheme.inversePrimary,
                    iconColor = MaterialTheme.colorScheme.primary,
                    label = "Edit Profile",
                    onClick = onEditProfileClick,
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    label = "Privacy & Data",
                    onClick = onPrivacyAndDataClick,
                )
            }

            // Data & Account Section
            SectionCard(title = "Data & Account") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Delete,
                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                    iconColor = MaterialTheme.colorScheme.error,
                    label = "Clear All Data",
                    labelColor = MaterialTheme.colorScheme.error,
                    showArrow = false,
                    onClick = { showClearDataDialog = true },
                )
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Outlined.ExitToApp,
                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                    iconColor = MaterialTheme.colorScheme.error,
                    label = "Log Out",
                    labelColor = MaterialTheme.colorScheme.error,
                    isLast = true,
                    showArrow = false,
                    onClick = { showLogoutDialog = true },
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.md))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, number: String, label: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MaterialTheme.dimens.radiusLg),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = MaterialTheme.dimens.elevationSm,
    ) {
        Column(
            modifier = Modifier.padding(vertical = MaterialTheme.dimens.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(vertical = MaterialTheme.dimens.sm)
                .clip(RoundedCornerShape(MaterialTheme.dimens.radiusXl))
                .background(MaterialTheme.colorScheme.onPrimary)
                .border(
                    MaterialTheme.dimens.borderThin,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    RoundedCornerShape(MaterialTheme.dimens.radiusXl),
                )
    ) {
        Text(
            text = title.uppercase(),
            modifier =
                Modifier.padding(
                    start = MaterialTheme.dimens.md,
                    top = MaterialTheme.dimens.md,
                    bottom = MaterialTheme.dimens.sm,
                ),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    isLast: Boolean = false,
    showArrow: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = MaterialTheme.dimens.md, vertical = MaterialTheme.dimens.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(MaterialTheme.dimens.grid45)
                    .clip(RoundedCornerShape(MaterialTheme.dimens.radiusXs))
                    .background(iconContainerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.iconMd),
                tint = iconColor,
            )
        }

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.md))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor,
        )

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.grid2),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.md),
            thickness = MaterialTheme.dimens.borderNormal,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    DermAssistTheme { ProfileScreen(onEditProfileClick = {}, onPrivacyAndDataClick = {}) }
}
