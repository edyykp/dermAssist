package com.ehealth.dermassist.ui.features.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.ui.theme.*
import java.io.File
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    user: User? = null,
    onScanSuccess: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val dimens = MaterialTheme.dimens
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    viewModel.processImage(tempUri)
                }
            },
        )

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                if (uri != null) {
                    viewModel.processImage(uri)
                }
            },
        )

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Long)
        }
    }

    LaunchedEffect(Unit) { viewModel.scanSuccessEvent.collectLatest { onScanSuccess() } }

    // Root container with background color to eliminate white padding gaps
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = MaterialTheme.dimens.md)
                    .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimens.md))

            HomeHeader(user = user)

            Spacer(modifier = Modifier.height(dimens.grid25))

            HeroScanCard(
                onTakePhoto = {
                    val file =
                        File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                    val uri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    tempUri = uri
                    cameraLauncher.launch(uri)
                },
                onUploadGallery = { galleryLauncher.launch("image/*") },
            )

            Spacer(modifier = Modifier.height(dimens.lg))

            HowItWorksCard()

            Spacer(modifier = Modifier.height(dimens.lg))

            DailyTipCard()

            Spacer(modifier = Modifier.height(dimens.lg))
        }

        // Host snackbar directly in the Box to avoid nested Scaffold padding issues
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = dimens.md),
        )
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader(user: User? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Good morning,",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${user?.name} 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Box(
            modifier =
                Modifier.size(MaterialTheme.dimens.grid5) // 44dp
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
                text = user?.getInitials()?.uppercase() ?: "",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─── Hero Scan Card ───────────────────────────────────────────────────────────
@Composable
private fun HeroScanCard(onTakePhoto: () -> Unit, onUploadGallery: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(dimens.radiusXxl))
                .background(
                    brush =
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary, // #1A6E5C
                                    Color(0xFF1A5C8C), // deep teal-blue end
                                )
                        )
                )
                .padding(horizontal = dimens.md, vertical = dimens.lg)
    ) {
        Column {
            Text(
                text = "AI SKIN ANALYSIS",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(dimens.grid075))
            Text(
                text = "Ready for today's skin check?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                lineHeight = 26.sp,
            )
            Spacer(modifier = Modifier.height(dimens.grid075))
            Text(
                text = "Get instant analysis and personalized tips in under 30 seconds.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(dimens.grid25))
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.grid125)) {

                // Primary CTA — Take Photo
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(dimens.radiusHuge))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable(onClick = onTakePhoto)
                            .padding(horizontal = dimens.grid225, vertical = dimens.grid125)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.grid075),
                    ) {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = "Take Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimens.iconSm),
                        )
                        Text(
                            text = "Take Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // Secondary CTA — Gallery
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(dimens.radiusHuge))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                            .clickable(onClick = onUploadGallery)
                            .padding(horizontal = dimens.grid225, vertical = dimens.grid125)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimens.grid075),
                    ) {
                        Icon(
                            Icons.Outlined.Image,
                            contentDescription = "Upload from Gallery",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(dimens.iconSm),
                        )
                        Text(
                            text = "Gallery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HowItWorksCard() {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.elevationSm),
    ) {
        Column(modifier = Modifier.padding(dimens.grid25)) {
            Text(
                text = "How it works",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(dimens.grid175))

            val steps =
                listOf(
                    "Take or upload a photo" to "Clear, well-lit close-up works best",
                    "AI analyzes your skin" to "Detection in under 5 seconds",
                    "Get your report" to "Conditions and insights of your diagnoses",
                )

            steps.forEachIndexed { index, (title, desc) ->
                HowItWorksStep(number = index + 1, title = title, description = desc)
                if (index < steps.lastIndex) Spacer(modifier = Modifier.height(dimens.grid15))
            }
        }
    }
}

@Composable
private fun HowItWorksStep(number: Int, title: String, description: String) {
    val dimens = MaterialTheme.dimens

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.grid15),
        verticalAlignment = Alignment.Top,
    ) {
        // Numbered badge
        Box(
            modifier =
                Modifier.size(dimens.grid3)
                    .clip(RoundedCornerShape(dimens.radiusSm))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimens.xxs),
            )
        }
    }
}

@Composable
private fun DailyTipCard() {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.elevationSm),
    ) {
        Column(
            modifier =
                Modifier.padding(dimens.grid25)
                    .border(
                        dimens.borderThin,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(dimens.radiusXl),
                    )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Skin health tips",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = TipYellow,
                    modifier = Modifier.size(dimens.iconSm),
                )
            }

            Spacer(modifier = Modifier.height(dimens.grid15))

            Text(
                text = "Daily sun protection",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text =
                    "Apply SPF 30+ daily to protect your skin from harmful UV rays, even on cloudy days.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimens.xxs),
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    DermAssistTheme { HomeScreen() }
}
