package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.AlertNotificationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: MainViewModel,
    onNavigateToLeads: () -> Unit,
    onNavigateToBeats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.allAlerts.collectAsState()
    val unreadCount by viewModel.unreadAlertsCount.collectAsState()

    Scaffold(
        modifier = modifier.testTag("notifications_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Alerts & Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$unreadCount unread priority alerts",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (unreadCount > 0) {
                        TextButton(
                            onClick = { viewModel.markAllAlertsRead() },
                            modifier = Modifier.testTag("mark_all_read_button")
                        ) {
                            Text("Mark All Read", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (alerts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No alerts at this moment", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                items(alerts) { alert ->
                    AlertNotificationCard(
                        alert = alert,
                        onAction = {
                            if (alert.type == "DEADLINE_ALERT") onNavigateToLeads()
                            else if (alert.type == "BEAT_APPROVAL") onNavigateToBeats()
                            else onNavigateToLeads()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertNotificationCard(
    alert: AlertNotificationEntity,
    onAction: () -> Unit
) {
    val isCritical = alert.priority == "CRITICAL"
    val isHigh = alert.priority == "HIGH"

    val iconColor = when {
        isCritical -> AlertCritical
        isHigh -> WarningAmber
        alert.type == "TARGET_ACHIEVED" -> SuccessGreen
        else -> BrandSecondaryLight
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCritical) AlertCritical.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alert_card_${alert.id.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (alert.type) {
                        "DEADLINE_ALERT" -> Icons.Default.Alarm
                        "BEAT_APPROVAL" -> Icons.Default.DirectionsWalk
                        "PROSPECT_APPROVAL" -> Icons.Default.Storefront
                        "TARGET_ACHIEVED" -> Icons.Default.EmojiEvents
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCritical) AlertCritical else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = alert.timestamp,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isCritical || isHigh) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onAction,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Take Immediate Action →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = iconColor)
                    }
                }
            }
        }
    }
}
