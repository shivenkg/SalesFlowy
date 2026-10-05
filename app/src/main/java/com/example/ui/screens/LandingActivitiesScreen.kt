package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.util.IndiaLocaleUtil
import com.example.ui.components.MetricCard
import com.example.ui.components.OfflineSyncBanner
import com.example.ui.components.PipelineFunnelBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LandingActivitiesScreen(
    viewModel: MainViewModel,
    onNavigateToStopExecution: (BeatStopEntity) -> Unit,
    onNavigateToBeats: () -> Unit,
    onNavigateToLeads: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onOpenSyncCenter: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val pendingSync by viewModel.pendingSyncItems.collectAsState()
    val todayStops by viewModel.todayStops.collectAsState()
    val allLeads by viewModel.allLeads.collectAsState()
    val alerts by viewModel.allAlerts.collectAsState()
    val unreadCount by viewModel.unreadAlertsCount.collectAsState()
    val gpsCoords by viewModel.currentGpsLocation.collectAsState()
    val gpsAccuracy by viewModel.gpsAccuracy.collectAsState()

    // Find critical deadline alert
    val criticalAlert = alerts.firstOrNull { it.priority == "CRITICAL" && !it.isRead } ?: alerts.firstOrNull { it.type == "DEADLINE_ALERT" }

    val user = currentUser ?: return
    val achievedPercentage = if (user.targetMonthly > 0) (user.achievedMonthly / user.targetMonthly).toFloat() else 0.84f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("landing_activities_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Offline Sync Status Banner
        item {
            OfflineSyncBanner(
                isOffline = isOffline,
                isSyncing = isSyncing,
                pendingCount = pendingSync.size,
                onToggleOffline = { viewModel.toggleOfflineMode() },
                onSyncNow = { viewModel.syncNow() },
                onOpenSyncCenter = onOpenSyncCenter
            )
        }

        // Executive Greeting & GPS Live Telemetry Pill
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Good Morning, ${user.name} 👋",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${user.role.replace("_", " ")} • ${user.spokeId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Live GPS telemetry badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SuccessGreen.copy(alpha = 0.15f),
                            modifier = Modifier.testTag("gps_telemetry_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Location Active (Background)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Day Started: 09:30 AM", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("Accuracy: $gpsAccuracy", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }

        // Custom High-Priority Lead Conversion Deadline Alert Banner
        if (criticalAlert != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertCritical.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AlertCritical.copy(alpha = 0.5f))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigateToAlerts() }
                        .testTag("critical_deadline_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AlertCritical),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Alert",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = criticalAlert.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertCritical
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AlertCritical,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = "URGENT",
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = criticalAlert.message,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View Lead",
                            tint = AlertCritical,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Section Title: Sales Target vs Achieved Metrics
        item {
            Text(
                text = "Sales Target vs. Achieved Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp)
            )
        }

        // Metrics Grid (2x2)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Monthly Revenue",
                        value = "${IndiaLocaleUtil.formatInr(user.achievedMonthly)} / ${IndiaLocaleUtil.formatInr(user.targetMonthly)}",
                        subtitle = "${(achievedPercentage * 100).toInt()}% Target Achieved",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = SuccessGreen,
                        progress = achievedPercentage,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Today's Collection",
                        value = "₹24,500 / ₹65,000",
                        subtitle = "37% of Day Goal",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = BrandSecondaryLight,
                        progress = 0.37f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val completed = todayStops.count { it.status == VisitStatus.COMPLETED.name }
                    val total = todayStops.size.coerceAtLeast(1)
                    MetricCard(
                        title = "Beat Visits",
                        value = "$completed of $total Stops",
                        subtitle = "${((completed.toFloat() / total) * 100).toInt()}% Route Complete",
                        icon = Icons.Default.DirectionsWalk,
                        accentColor = WarningAmber,
                        progress = completed.toFloat() / total,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pipeline Conversion",
                        value = "84%",
                        subtitle = "+6% vs Last Month",
                        icon = Icons.Default.TrendingUp,
                        accentColor = PurpleAccent,
                        progress = 0.84f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Performance Analytics Visualizers (Funnel & Adherence)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("pipeline_funnel_visualizer")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pipeline Conversion Analytics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToLeads) {
                            Text("View All Leads", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    PipelineFunnelBar(
                        stageName = "1. Active Prospects",
                        count = allLeads.count { it.stage == LeadStage.PROSPECT.name },
                        percentage = 1.0f,
                        barColor = BrandPrimaryLight,
                        amountStr = "$28k"
                    )
                    PipelineFunnelBar(
                        stageName = "2. Needs Qualified",
                        count = allLeads.count { it.stage == LeadStage.QUALIFIED.name },
                        percentage = 0.75f,
                        barColor = BrandSecondaryLight,
                        amountStr = "$45k"
                    )
                    PipelineFunnelBar(
                        stageName = "3. Commercial Proposals",
                        count = allLeads.count { it.stage == LeadStage.PROPOSAL.name },
                        percentage = 0.55f,
                        barColor = PurpleAccent,
                        amountStr = "$120k"
                    )
                    PipelineFunnelBar(
                        stageName = "4. Active Negotiations",
                        count = allLeads.count { it.stage == LeadStage.NEGOTIATION.name },
                        percentage = 0.35f,
                        barColor = WarningAmber,
                        amountStr = "$75k"
                    )
                    PipelineFunnelBar(
                        stageName = "5. Closed Deals (Won)",
                        count = allLeads.count { it.stage == LeadStage.WON.name },
                        percentage = 0.25f,
                        barColor = SuccessGreen,
                        amountStr = "$95k"
                    )
                }
            }
        }

        // Section Title: Today's Visit Plan (Based on Approved Beat)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Visit Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Connaught Core Commercial Beat • Approved by Vikram (Area Mgr)",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onNavigateToBeats) {
                    Text("Beat Plans", fontSize = 12.sp)
                }
            }
        }

        // List of Today's Beat Stops
        if (todayStops.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No beat stops scheduled for today. Create and submit a beat plan for manager approval.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(todayStops) { stop ->
                BeatStopCard(
                    stop = stop,
                    onStartVisit = { onNavigateToStopExecution(stop) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Quick Bottom Action Row (New Lead / New Merchant Onboarding)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToLeads,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_new_lead_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Lead", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToOnboarding,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_onboard_customer_button")
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Onboard Merchant", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BeatStopCard(
    stop: BeatStopEntity,
    onStartVisit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = stop.status == VisitStatus.COMPLETED.name
    val isCheckedIn = stop.status == VisitStatus.CHECKED_IN.name

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else if (isCheckedIn) {
                BrandSecondaryLight.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCheckedIn) BrandSecondaryLight else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("beat_stop_card_${stop.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) SuccessGreen
                                else if (isCheckedIn) BrandSecondaryLight
                                else BrandPrimaryLight.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = "${stop.stopOrder}",
                                fontWeight = FontWeight.Bold,
                                color = if (isCheckedIn) Color.White else MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = stop.customerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${stop.customerCategory} • Scheduled: ${stop.plannedTime}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Chip
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (stop.isOfflineRecorded) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WarningAmber.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = if (isCompleted) "Completed" else if (isCheckedIn) "Active Visit" else "Pending",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) SuccessGreen else if (isCheckedIn) BrandSecondaryLight else WarningAmber
                            )
                        },
                        modifier = Modifier.height(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Address & GPS Pin
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stop.address,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Completed visit stats or Action button
            if (isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Checked In: ${stop.checkInTime} • Out: ${stop.checkOutTime}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Order: ₹${stop.orderValue.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onStartVisit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCheckedIn) WarningAmber else BrandPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("start_visit_button_${stop.id.lowercase()}")
                ) {
                    Icon(
                        imageVector = if (isCheckedIn) Icons.Default.EditNote else Icons.Default.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCheckedIn) "Resume Visit & Record Interaction" else "Capture GPS & Check-In",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
