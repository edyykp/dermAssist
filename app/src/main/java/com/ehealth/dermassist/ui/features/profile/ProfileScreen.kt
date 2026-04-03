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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.ui.theme.*

@Composable
fun ProfileScreen(viewModel: ProfileScreenViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize().background(SurfaceLight)) {
        // Content
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.displayMedium,
                    color = DarkText,
                    fontSize = 22.sp,
                )
            }

            // Profile Card
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors = listOf(BackgroundGradientStart, Color(0xFFE4EEF9))
                                )
                        )
                        .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Avatar
                    Box(
                        modifier =
                            Modifier.size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    brush =
                                        Brush.linearGradient(
                                            colors = listOf(PrimaryGreen, PrimaryBlue)
                                        )
                                )
                                .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "SJ",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sarah Johnson",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 18.sp,
                            color = DarkText,
                        )
                        Text(
                            text = "Age 28 · Joined Jan 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = BodyText,
                            fontSize = 13.sp,
                        )
                    }
                }

                // Edit Button
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                ) {
                    Text(
                        text = "Edit",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontSize = 12.sp,
                    )
                }
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(modifier = Modifier.weight(1f), number = "12", label = "Total Scans")
            }

            // Account Section
            SectionCard(title = "Account") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    iconContainerColor = IconBgGreen,
                    iconColor = PrimaryGreen,
                    label = "Edit Profile",
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock, // Placeholder for Privacy
                    iconContainerColor = IconBgPurple,
                    iconColor = PillPurple,
                    label = "Privacy & Data",
                )
            }

            // Data & Account Section
            SectionCard(title = "Data & Account") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Delete, // Placeholder for Clear Data
                    iconContainerColor = IconBgRed,
                    iconColor = ErrorRed,
                    label = "Clear All Data",
                    labelColor = ErrorRed,
                    showArrow = false,
                )
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Outlined.ExitToApp, // Placeholder for Logout
                    iconContainerColor = IconBgRed,
                    iconColor = ErrorRed,
                    label = "Log Out",
                    labelColor = ErrorRed,
                    isLast = true,
                    showArrow = false,
                    onClick = { viewModel.logout() },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, number: String, label: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = number,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
            )
            Text(text = label, fontSize = 11.sp, color = BodyText, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(0.5.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
    ) {
        Text(
            text = title.uppercase(),
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = BodyText,
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
    labelColor: Color = DarkText,
    isLast: Boolean = false,
    showArrow: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconContainerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = iconColor,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

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
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFB0B4B6),
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = BorderColor,
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    DermAssistTheme { ProfileScreen() }
}
