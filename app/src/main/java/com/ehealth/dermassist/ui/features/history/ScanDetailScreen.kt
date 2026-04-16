package com.ehealth.dermassist.ui.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.features.report.model.SkinCondition
import com.ehealth.dermassist.ui.features.report.model.SkinMetric
import com.ehealth.dermassist.ui.features.report.model.SkinRecommendation
import com.ehealth.dermassist.ui.theme.*

@Composable
fun ScanDetailScreen(
    userId: String,
    scanId: String,
    onBackClick: () -> Unit,
    viewModel: ScanDetailViewModel = hiltViewModel(),
) {
    val dimens = MaterialTheme.dimens
    val loading by viewModel.isLoading.collectAsState()
    val report by viewModel.scanReport.collectAsState()

    LaunchedEffect(userId, scanId) { viewModel.loadScanDetail(userId, scanId) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimens.md))

            ScanDetailTopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(dimens.md))

            report?.let { reportData ->
                ScanDateBadge(date = reportData.scanDate)

                Spacer(modifier = Modifier.height(dimens.md))

                ScanPreviewCard(
                    scanArea = reportData.scanArea,
                    overallScore = reportData.overallScore,
                )

                Spacer(modifier = Modifier.height(dimens.md))

                DetectedConditionsSection(conditions = reportData.conditions)

                Spacer(modifier = Modifier.height(dimens.md))

                SkinMetricsCard(metrics = reportData.metrics)

                Spacer(modifier = Modifier.height(dimens.md))

                RecommendationsCard(recommendations = reportData.recommendations)
            }

            Spacer(modifier = Modifier.height(dimens.grid3))
        }

        if (loading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun ScanDetailTopBar(onBackClick: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = "Scan Details",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun ScanDateBadge(date: String) {
    val dimens = MaterialTheme.dimens
    Box(
        modifier =
            Modifier.padding(horizontal = dimens.grid25)
                .clip(RoundedCornerShape(dimens.radiusHuge))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = dimens.grid175, vertical = dimens.grid075)
    ) {
        Text(
            text = "Captured on $date",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

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
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Scan preview",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(dimens.xl).align(Alignment.Center),
                )
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
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(dimens.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Skin Health Score",
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

    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(dimens.radiusHuge))
                .background(condition.bgColor)
                .padding(horizontal = dimens.grid175, vertical = dimens.grid075)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.grid075),
        ) {
            Box(
                modifier =
                    Modifier.size(dimens.grid075)
                        .clip(RoundedCornerShape(50))
                        .background(condition.textColor)
            )
            Text(
                text = condition.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = condition.textColor,
            )
        }
    }
}

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
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.grid175))
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
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(dimens.sm)
                    .clip(RoundedCornerShape(dimens.xs))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
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
