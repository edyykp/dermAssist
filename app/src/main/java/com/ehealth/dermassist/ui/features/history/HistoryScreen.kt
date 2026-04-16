package com.ehealth.dermassist.ui.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ehealth.dermassist.domain.model.HistoryConditionTag
import com.ehealth.dermassist.domain.model.ScanHistoryItem
import com.ehealth.dermassist.ui.components.ButtonVariant
import com.ehealth.dermassist.ui.components.DermButton
import com.ehealth.dermassist.ui.components.LoadingOverlay
import com.ehealth.dermassist.ui.theme.*

@Composable
fun HistoryScreen(
    userId: String,
    onScanClick: (ScanHistoryItem) -> Unit = {},
    onTakeFirstScanClick: () -> Unit = {},
    viewModel: HistoryScreenViewModel = hiltViewModel(),
) {
    val dimens = MaterialTheme.dimens
    val scans by viewModel.scans.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

    // Trigger loading when userId changes and is valid
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.start(userId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Spacer(modifier = Modifier.height(dimens.md))

        // ── Page Header ───────────────────────────────────────────────────────
        HistoryHeader(totalScans = scans.size)

        Spacer(modifier = Modifier.height(dimens.md))

        // ── Scan List ─────────────────────────────────────────────────────────
        if (scans.isEmpty()) {
            HistoryEmptyState(onTakeFirstScanClick)
        } else {
            LazyColumn(
                contentPadding =
                    PaddingValues(
                        start = dimens.grid25,
                        end = dimens.grid25,
                        bottom = dimens.grid3,
                    ),
                verticalArrangement = Arrangement.spacedBy(dimens.grid125),
            ) {
                items(items = scans, key = { it.id }) { scan ->
                    ScanHistoryCard(scan = scan, onClick = { onScanClick(scan) })
                }
            }
        }
    }

    if (loading) {
        LoadingOverlay()
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun HistoryHeader(totalScans: Int) {
    val dimens = MaterialTheme.dimens

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.grid25),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Scan History",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "$totalScans scans recorded",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─── Scan History Card ────────────────────────────────────────────────────────

@Composable
private fun ScanHistoryCard(scan: ScanHistoryItem, onClick: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.radiusLg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(dimens.elevationSm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(dimens.grid175),
            horizontalArrangement = Arrangement.spacedBy(dimens.grid15),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Thumbnail ─────────────────────────────────────────────────────
            Box(
                modifier =
                    Modifier.size(dimens.grid7)
                        .clip(RoundedCornerShape(dimens.radiusMd))
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            scan.accentBgColor,
                                            scan.accentBgColor.copy(alpha = 0.6f),
                                        )
                                )
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = null,
                    tint = scan.accentColor,
                    modifier = Modifier.size(dimens.grid3),
                )
            }

            // ── Info ──────────────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                // Date + time
                Text(
                    text = "${scan.date} · ${scan.time}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(dimens.xxs))
                // Score
                Text(
                    text = scan.scanTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(dimens.xs))
                // Condition tags
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.grid075)) {
                    scan.conditions.forEach { tag -> HistoryTag(tag = tag) }
                }
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimens.iconLg),
            )
        }
    }
}

// ─── History Tag ──────────────────────────────────────────────────────────────

@Composable
private fun HistoryTag(tag: HistoryConditionTag) {
    val dimens = MaterialTheme.dimens

    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(dimens.radiusSm))
                .background(tag.bgColor)
                .padding(horizontal = dimens.sm, vertical = dimens.xxs)
    ) {
        Text(
            text = tag.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = tag.textColor,
        )
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun HistoryEmptyState(onTakeFirstScanClick: () -> Unit) {
    val dimens = MaterialTheme.dimens

    Column(
        modifier = Modifier.fillMaxWidth().padding(dimens.grid6),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.md),
    ) {
        Box(
            modifier =
                Modifier.size(dimens.grid9)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimens.grid45),
            )
        }
        Text(
            text = "No scans yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Your scan history will appear here.\nTake your first skin scan to get started.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
        DermButton(
            text = "Take your first scan",
            variant = ButtonVariant.Secondary,
            onClick = onTakeFirstScanClick,
            modifier = Modifier,
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryScreenPreview() {
    DermAssistTheme { HistoryScreen(userId = "") }
}

// Preview empty state
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryScreenEmptyPreview() {
    DermAssistTheme { HistoryScreen(userId = "") }
}
