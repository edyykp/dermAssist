package com.ehealth.dermassist.ui.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.ui.components.DermButton
import com.ehealth.dermassist.ui.components.GoogleButton
import com.ehealth.dermassist.ui.features.auth.AuthViewModel
import com.ehealth.dermassist.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onSignUpLogin: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    Color(0xFFF0F7FF),
                                    MaterialTheme.colorScheme.background,
                                )
                        )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
    ) {
        // Scrollable Top Content
        Column(
            modifier =
                Modifier.weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.dimens.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.xl))

            // Logo
            Box(
                modifier =
                    Modifier.size(MaterialTheme.dimens.logoSize)
                        .clip(RoundedCornerShape(MaterialTheme.dimens.radiusXl))
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
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(MaterialTheme.dimens.iconXl),
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.lg))

            Text(
                text = "DermAssist",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "AI SKIN ANALYSIS",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.lg))

            Text(
                text = "Your skin deserves expert attention, every day.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.md))

            Text(
                text =
                    "Get instant AI-powered skin analysis, personalized recommendations, and track your skin health over time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.lg))

            // Pills
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.sm),
                maxItemsInEachRow = 2,
            ) {
                PillItem(color = MaterialTheme.colorScheme.primary, text = "AI-powered scan")
                PillItem(color = MaterialTheme.colorScheme.secondary, text = "Condition detection")
                PillItem(color = MaterialTheme.colorScheme.tertiary, text = "Progress tracking")
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.lg))
        }

        // Fixed Bottom CTA Section
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.lg)
                    .padding(bottom = MaterialTheme.dimens.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.md),
        ) {
            GoogleButton { authViewModel.handleGoogleSignIn(context, onNavigateToHome) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimens.md),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            DermButton(text = "Sign Up / Log In", onClick = onSignUpLogin)

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.md))

            // Terms
            Text(
                text =
                    buildAnnotatedString {
                        append("By continuing, you agree to our ")
                        withStyle(
                            style =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                )
                        ) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(
                            style =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                )
                        ) {
                            append("Privacy Policy")
                        }
                        append(".")
                    },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier.clickable {
                        // Logic to detect which part was clicked
                    },
            )
        }
    }
}

@Composable
fun PillItem(color: Color, text: String) {
    Surface(
        shape = RoundedCornerShape(MaterialTheme.dimens.radiusXl),
        border =
            androidx.compose.foundation.BorderStroke(
                MaterialTheme.dimens.borderThick,
                MaterialTheme.colorScheme.outline,
            ),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = MaterialTheme.dimens.md,
                    vertical = MaterialTheme.dimens.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.xs),
        ) {
            Box(
                modifier =
                    Modifier.size(MaterialTheme.dimens.indicatorSize).background(color, CircleShape)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    DermAssistTheme {
        SplashScreen(
            onNavigateToHome = {},
            onSignUpLogin = {},
            onTermsClick = {},
            onPrivacyClick = {},
        )
    }
}
