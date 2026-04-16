package com.ehealth.dermassist.ui.features.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.features.report.model.ConditionSeverity
import com.ehealth.dermassist.ui.features.report.model.SkinCondition
import com.ehealth.dermassist.ui.features.report.model.SkinMetric
import com.ehealth.dermassist.ui.features.report.model.SkinRecommendation
import com.ehealth.dermassist.ui.features.report.model.SkinReport
import com.ehealth.dermassist.ui.theme.*

val sampleReport =
    SkinReport(
        scanDate = "Mar 27, 2026",
        scanArea = "Left cheek scan",
        overallScore = 74,
        conditions =
            listOf(
                SkinCondition("Mild Acne", ConditionSeverity.CONCERN),
                SkinCondition("Some Redness", ConditionSeverity.MODERATE),
                SkinCondition("Good Hydration", ConditionSeverity.GOOD),
                SkinCondition("Low Oiliness", ConditionSeverity.GOOD),
            ),
        metrics =
            listOf(
                SkinMetric("Hydration", 82, PrimaryGreen),
                SkinMetric("Oiliness", 24, PrimaryBlue),
                SkinMetric("Redness", 41, Color(0xFFF59E0B)),
                SkinMetric("Acne Score", 35, ErrorRed),
            ),
        recommendations =
            listOf(
                SkinRecommendation(
                    title = "Use a gentle cleanser twice daily",
                    description = "Opt for pH-balanced formulas to support your skin barrier.",
                    icon = Icons.Outlined.CheckCircle,
                    iconBg = IconBgGreen,
                    iconTint = PrimaryGreen,
                ),
                SkinRecommendation(
                    title = "Apply SPF 30+ every morning",
                    description = "Protect against UV-induced redness and hyperpigmentation.",
                    icon = Icons.Outlined.WbSunny,
                    iconBg = IconBgBlue,
                    iconTint = PrimaryBlue,
                ),
                SkinRecommendation(
                    title = "Avoid touching your face",
                    description = "Reduces bacteria transfer that contributes to breakouts.",
                    icon = Icons.Outlined.Warning,
                    iconBg = IconBgRed,
                    iconTint = ErrorRed,
                ),
            ),
    )

// ─── Report Screen ────────────────────────────────────────────────────────────

@Composable
fun ReportScreen(
    userId: String,
    report: SkinReport = sampleReport,
    onRescanClick: () -> Unit = {},
    viewModel: ReportScreenViewModel = hiltViewModel(),
) {
    val dimens = MaterialTheme.dimens
    val loading by viewModel.isLoading.collectAsState()
    val latest by viewModel.latestScan.collectAsState(initial = userId)

    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(dimens.md))

        // ── Top Bar ───────────────────────────────────────────────────────────
        ReportTopBar(date = report.scanDate)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Scan Preview Card ─────────────────────────────────────────────────
        ScanPreviewCard(scanArea = report.scanArea, overallScore = report.overallScore)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Detected Conditions ───────────────────────────────────────────────
        DetectedConditionsSection(conditions = report.conditions)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Skin Metrics ──────────────────────────────────────────────────────
        SkinMetricsCard(metrics = report.metrics)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Recommendations ───────────────────────────────────────────────────
        RecommendationsCard(recommendations = report.recommendations)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Re-scan CTA ───────────────────────────────────────────────────────
        RescanButton(onClick = onRescanClick)

        Spacer(modifier = Modifier.height(dimens.grid3))
    }

    if (loading) {
        LoadingOverlay()
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun ReportTopBar(date: String) {
    val dimens = MaterialTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Skin Report",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Date chip
            Box(
                modifier =
                    Modifier.clip(RoundedCornerShape(dimens.radiusHuge))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = dimens.grid175, vertical = dimens.grid075)
            ) {
                Text(
                    text = date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Scan Preview Card ────────────────────────────────────────────────────────

@Composable
private fun ScanPreviewCard(scanArea: String, overallScore: Int) {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        shape = RoundedCornerShape(dimens.radiusXxl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(dimens.elevationSm),
    ) {
        Column {
            // Image placeholder with gradient overlay
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(160.dp)
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.secondaryContainer,
                                        )
                                )
                        )
            ) {
                // Placeholder scan icon
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Scan preview",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(dimens.xl).align(Alignment.Center),
                )
                // Bottom overlay bar
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(
                                brush =
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0x66000000))
                                    )
                            )
                            .padding(horizontal = dimens.md, vertical = dimens.grid15),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = scanArea,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Box(
                        modifier =
                            Modifier.clip(RoundedCornerShape(dimens.radiusMd))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = dimens.grid125, vertical = dimens.xs)
                    ) {
                        Text(
                            text = "✓ Analyzed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }

            // Score row
            Row(
                modifier = Modifier.fillMaxWidth().padding(dimens.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Overall Skin Score",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(dimens.radiusMd))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = dimens.grid175, vertical = dimens.grid075)
                ) {
                    Text(
                        text = "$overallScore / 100",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

// ─── Detected Conditions ──────────────────────────────────────────────────────

@Composable
private fun DetectedConditionsSection(conditions: List<SkinCondition>) {
    val dimens = MaterialTheme.dimens

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25)) {
        Text(
            text = "Detected Conditions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(dimens.sm))
        // Wrap chips in two rows manually to avoid FlowRow needing extra dependency
        val chunked = conditions.chunked(2)
        chunked.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.sm),
                modifier = Modifier.padding(bottom = dimens.sm),
            ) {
                row.forEach { condition -> ConditionChip(condition = condition) }
            }
        }
    }
}

@Composable
private fun ConditionChip(condition: SkinCondition) {
    val dimens = MaterialTheme.dimens

    val (bgColor, dotColor, textColor) =
        when (condition.severity) {
            ConditionSeverity.CONCERN -> Triple(IconBgRed, ErrorRed, ErrorRed)
            ConditionSeverity.MODERATE -> Triple(BadgeOrangeBg, BadgeOrangeText, BadgeOrangeText)
            ConditionSeverity.GOOD -> Triple(IconBgGreen, PrimaryGreen, PrimaryGreen)
        }

    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(dimens.radiusHuge))
                .background(bgColor)
                .padding(horizontal = dimens.grid175, vertical = dimens.grid075)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.grid075),
        ) {
            Box(
                modifier =
                    Modifier.size(dimens.grid075).clip(RoundedCornerShape(50)).background(dotColor)
            )
            Text(
                text = condition.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
            )
        }
    }
}

// ─── Skin Metrics Card ────────────────────────────────────────────────────────

@Composable
private fun SkinMetricsCard(metrics: List<SkinMetric>) {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        shape = RoundedCornerShape(dimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(dimens.elevationSm),
    ) {
        Column(modifier = Modifier.padding(dimens.md)) {
            Text(
                text = "Skin Metrics",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(dimens.md))
            metrics.forEachIndexed { index, metric ->
                SkinMetricRow(metric = metric)
                if (index < metrics.lastIndex) {
                    Spacer(modifier = Modifier.height(dimens.grid175))
                }
            }
        }
    }
}

@Composable
private fun SkinMetricRow(metric: SkinMetric) {
    val dimens = MaterialTheme.dimens

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = metric.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "${metric.value}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = metric.color,
            )
        }
        Spacer(modifier = Modifier.height(dimens.grid075))
        // Track
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(dimens.sm)
                    .clip(RoundedCornerShape(dimens.xs))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Fill
            Box(
                modifier =
                    Modifier.fillMaxWidth(metric.value / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(dimens.xs))
                        .background(metric.color)
            )
        }
    }
}

// ─── Recommendations Card ─────────────────────────────────────────────────────

@Composable
private fun RecommendationsCard(recommendations: List<SkinRecommendation>) {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        shape = RoundedCornerShape(dimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(dimens.elevationSm),
    ) {
        Column(modifier = Modifier.padding(dimens.md)) {
            Text(
                text = "Recommendations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(dimens.md))
            recommendations.forEachIndexed { index, rec ->
                RecommendationRow(recommendation = rec)
                if (index < recommendations.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = dimens.grid15),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        thickness = dimens.borderThin,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationRow(recommendation: SkinRecommendation) {
    val dimens = MaterialTheme.dimens

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.grid125),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier.size(dimens.grid4)
                    .clip(RoundedCornerShape(dimens.radiusSm))
                    .background(recommendation.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = recommendation.icon,
                contentDescription = null,
                tint = recommendation.iconTint,
                modifier = Modifier.size(dimens.iconMd),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recommendation.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(dimens.xxs))
            Text(
                text = recommendation.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
        }
    }
}

// ─── Re-scan Button ───────────────────────────────────────────────────────────

@Composable
private fun RescanButton(onClick: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Button(
        onClick = onClick,
        modifier =
            Modifier.fillMaxWidth().padding(horizontal = dimens.grid25).height(dimens.buttonHeight),
        shape = RoundedCornerShape(dimens.radiusHuge),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Icon(
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(dimens.iconSm),
        )
        Spacer(modifier = Modifier.width(dimens.sm))
        Text(text = "Take New Scan", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportScreenPreview() {
    DermAssistTheme { ReportScreen(userId = "test") }
}
