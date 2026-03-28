package com.example.dermassist.ui.features.splash

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dermassist.R
import com.example.dermassist.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplashScreen(onContinueWithGoogle: () -> Unit, onSignUpLogin: () -> Unit) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    BackgroundGradientStart,
                                    BackgroundGradientMid,
                                    BackgroundWhite,
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
                    .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Logo
            Box(
                modifier =
                    Modifier.size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(colors = listOf(PrimaryGreen, PrimaryBlue))
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "DermAssist",
                style = MaterialTheme.typography.displayLarge,
                color = DarkText,
            )

            Text(
                text = "AI SKIN ANALYSIS",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryGreen,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "AI-powered dermatological analysis",
                style = MaterialTheme.typography.displayMedium,
                color = DarkText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text =
                    "Get instant AI-powered skin analysis, personalized recommendations, and track your skin health over time.",
                style = MaterialTheme.typography.bodyLarge,
                color = BodyText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Pills
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2,
            ) {
                PillItem(color = PillGreen, text = "AI-powered scan")
                PillItem(color = PillBlue, text = "Condition detection")
                PillItem(color = PillPurple, text = "Progress tracking")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Fixed Bottom CTA Section
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onContinueWithGoogle,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.variant_2),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        color = DarkText,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = BodyText,
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            }

            Button(
                onClick = onSignUpLogin,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SecondaryGreenBorder),
            ) {
                Text(
                    text = "Sign Up / Log In",
                    color = PrimaryGreen,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Terms
            Text(
                text =
                    buildAnnotatedString {
                        append("By continuing, you agree to our ")
                        withStyle(
                            style = SpanStyle(color = PrimaryGreen, fontWeight = FontWeight.Medium)
                        ) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(
                            style = SpanStyle(color = PrimaryGreen, fontWeight = FontWeight.Medium)
                        ) {
                            append("Privacy Policy")
                        }
                        append(".")
                    },
                style = MaterialTheme.typography.labelSmall,
                color = BodyText,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun PillItem(color: Color, text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderColor),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(modifier = Modifier.size(7.dp).background(color, CircleShape))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = BodyText)
        }
    }
}

@Composable
@Preview
private fun SplashScreenPreview() {
    DermAssistTheme { SplashScreen(onContinueWithGoogle = {}, onSignUpLogin = {}) }
}
