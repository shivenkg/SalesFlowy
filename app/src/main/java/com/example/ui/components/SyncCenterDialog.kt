package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.SyncQueueEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncCenterDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val isOffline by viewModel.isOffline.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val pendingItems by viewModel.pendingSyncItems.collectAsState()
    val lastSync by viewModel.lastSyncTimestamp.collectAsState()
    val mongoConfig by viewModel.mongoConfig.collectAsState()

    var showMongoConfig by remember { mutableStateOf(false) }
    var clusterInput by remember { mutableStateOf(mongoConfig.clusterName) }
    var dbInput by remember { mutableStateOf(mongoConfig.databaseName) }
    var apiKeyInput by remember { mutableStateOf(mongoConfig.apiKey) }
    var endpointInput by remember { mutableStateOf(mongoConfig.endpointUrl) }
    var pingStatusText by remember { mutableStateOf<String?>(null) }
    var isPinging by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("sync_center_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) WarningAmber.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isOffline) WarningAmber else SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sync & MongoDB Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOffline) "Local SQLite/Room Active" else "Connected to MongoDB Atlas",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Network simulation toggle card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOffline) WarningAmber.copy(alpha = 0.12f) else BrandSecondaryLight.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isOffline) "Simulating Disconnected Field Mode" else "Enterprise Cloud Connectivity Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (isOffline) "Activities saved in local Room SQLite cache." else "Last synced at $lastSync",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = !isOffline,
                            onCheckedChange = { viewModel.toggleOfflineMode() },
                            modifier = Modifier.testTag("network_toggle_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MongoDB Atlas Cloud Connection Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SuccessGreen.copy(alpha = 0.4f))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mongodb_connection_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = "MongoDB",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "MongoDB Atlas",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SuccessGreen
                                        ) {
                                            Text(
                                                text = if (mongoConfig.isConnected) "CONNECTED" else "STANDBY",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${mongoConfig.clusterName} • db: ${mongoConfig.databaseName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showMongoConfig = !showMongoConfig },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (showMongoConfig) Icons.Default.ExpandLess else Icons.Default.Settings,
                                    contentDescription = "Configure MongoDB",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stats Grid (Latency, Synced Docs, Collections)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Cluster Latency", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${mongoConfig.lastPingMs} ms", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Atlas Documents", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${mongoConfig.totalSyncedDocuments}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Collections", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("5 collections", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (pingStatusText != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pingStatusText ?: "",
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    isPinging = true
                                    viewModel.pingMongoDb { res ->
                                        isPinging = false
                                        pingStatusText = "${res.status}: ${res.message} (${res.latencyMs}ms)"
                                    }
                                },
                                enabled = !isPinging,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("ping_mongodb_button")
                            ) {
                                if (isPinging) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Ping", fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = { showMongoConfig = !showMongoConfig },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("toggle_mongodb_config_button")
                            ) {
                                Text(if (showMongoConfig) "Hide Setup" else "Cluster Config", fontSize = 11.sp)
                            }
                        }

                        // Collapsible MongoDB Atlas Configuration Inputs
                        if (showMongoConfig) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("MongoDB Atlas Connection Settings", fontWeight = FontWeight.Bold, fontSize = 11.sp)

                                OutlinedTextField(
                                    value = clusterInput,
                                    onValueChange = { clusterInput = it },
                                    label = { Text("Data Source / Cluster Name", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = dbInput,
                                    onValueChange = { dbInput = it },
                                    label = { Text("Database Name", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = apiKeyInput,
                                    onValueChange = { apiKeyInput = it },
                                    label = { Text("Atlas Data API Key / Secret", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = endpointInput,
                                    onValueChange = { endpointInput = it },
                                    label = { Text("Atlas Data API Endpoint URL", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.updateMongoDbConfig(clusterInput, dbInput, apiKeyInput, endpointInput)
                                        showMongoConfig = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .testTag("save_mongodb_config_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Save & Connect MongoDB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pending Queue Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Local Queue (${pendingItems.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (isSyncing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing to MongoDB...", fontSize = 11.sp, color = SuccessGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (pendingItems.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "All local records synchronized with MongoDB Atlas!",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pendingItems.take(4).forEach { item ->
                            SyncQueueRowItem(item = item, isSyncing = isSyncing)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action button: Force Sync Now to MongoDB
                Button(
                    onClick = { viewModel.syncNow() },
                    enabled = !isSyncing && !isOffline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dialog_sync_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOffline) "Connect Online to Synchronize" else "Push & Sync to MongoDB Atlas (${pendingItems.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SyncQueueRowItem(item: SyncQueueEntity, isSyncing: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BrandPrimaryLight.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.actionType) {
                        "CHECK_IN" -> Icons.Default.GpsFixed
                        "CHECK_OUT" -> Icons.Default.ReceiptLong
                        "CREATE_LEAD" -> Icons.Default.FilterAlt
                        "ONBOARD_CUSTOMER" -> Icons.Default.Storefront
                        else -> Icons.Default.Upload
                    },
                    contentDescription = null,
                    tint = BrandPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.entityTitle.ifEmpty { item.actionType },
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = item.payload,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSyncing) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (isSyncing) "SYNCING" else "QUEUED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSyncing) SuccessGreen else WarningAmber,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
