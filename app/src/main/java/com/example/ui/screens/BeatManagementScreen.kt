package com.example.ui.screens

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
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatManagementScreen(
    viewModel: MainViewModel,
    onNavigateToApprovals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val beatPlans by viewModel.allBeatPlans.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showCreateBeatDialog by remember { mutableStateOf(false) }

    val userRole = currentUser?.role ?: PersonaType.SPOKE_FRONTLINER.name
    val isManager = userRole == PersonaType.HUB_AREA_MANAGER.name ||
            userRole == PersonaType.REGIONAL_HEAD.name ||
            userRole == PersonaType.ZONAL_LEAD.name ||
            userRole == PersonaType.ADMIN.name

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateBeatDialog = true },
                icon = { Icon(Icons.Default.AddRoad, contentDescription = null) },
                text = { Text("Plan New Beat") },
                containerColor = BrandPrimaryLight,
                contentColor = Color.White,
                modifier = Modifier.testTag("plan_new_beat_fab")
            )
        },
        modifier = modifier.testTag("beat_management_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Beat Route Management",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Manager Prior Approval Workflow & Adherence",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isManager) {
                                FilledTonalButton(
                                    onClick = onNavigateToApprovals,
                                    modifier = Modifier.testTag("manager_approvals_shortcut_button")
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Approvals", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Approved", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${beatPlans.count { it.status == BeatStatus.APPROVED.name }}",
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    fontSize = 16.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pending Review", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${beatPlans.count { it.status == BeatStatus.PENDING_APPROVAL.name }}",
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber,
                                    fontSize = 16.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Completed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${beatPlans.count { it.status == BeatStatus.COMPLETED.name }}",
                                    fontWeight = FontWeight.Bold,
                                    color = InfoSky,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Beat Plans List
            items(beatPlans) { beat ->
                BeatPlanCard(
                    beat = beat,
                    isManager = isManager,
                    onApprove = { viewModel.reviewBeatPlan(beat.id, true, "Approved for field execution.") },
                    onReject = { viewModel.reviewBeatPlan(beat.id, false, "Route coverage needs rebalancing.") }
                )
            }
        }
    }

    if (showCreateBeatDialog) {
        CreateBeatPlanDialog(
            onDismiss = { showCreateBeatDialog = false },
            onSubmit = { title, date, targetCol ->
                val sampleStops = listOf(
                    BeatStopEntity(
                        id = "",
                        beatId = "",
                        stopOrder = 1,
                        customerName = "Apex Wholesale Mart",
                        customerCategory = "Wholesaler",
                        address = "Chawri Bazar, Old Delhi",
                        phone = "+91 98100 11223",
                        latitude = 28.6505,
                        longitude = 77.2301,
                        plannedTime = "10:30 AM"
                    ),
                    BeatStopEntity(
                        id = "",
                        beatId = "",
                        stopOrder = 2,
                        customerName = "Imperial Gourmet Emporium",
                        customerCategory = "Enterprise",
                        address = "Kashmere Gate Hub, Delhi",
                        phone = "+91 98200 22334",
                        latitude = 28.6665,
                        longitude = 77.2285,
                        plannedTime = "01:00 PM"
                    ),
                    BeatStopEntity(
                        id = "",
                        beatId = "",
                        stopOrder = 3,
                        customerName = "Sunrise FMCG Depot",
                        customerCategory = "Retailer",
                        address = "Civil Lines Market, Delhi",
                        phone = "+91 98300 33445",
                        latitude = 28.6750,
                        longitude = 77.2230,
                        plannedTime = "03:45 PM"
                    )
                )
                viewModel.submitBeatPlan(title, date, sampleStops, targetCol)
                showCreateBeatDialog = false
            }
        )
    }
}

@Composable
fun BeatPlanCard(
    beat: BeatPlanEntity,
    isManager: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val status = try {
        BeatStatus.valueOf(beat.status)
    } catch (_: Exception) {
        BeatStatus.PENDING_APPROVAL
    }

    val statusColor = when (status) {
        BeatStatus.APPROVED -> SuccessGreen
        BeatStatus.PENDING_APPROVAL -> WarningAmber
        BeatStatus.REJECTED -> AlertCritical
        BeatStatus.IN_PROGRESS -> BrandSecondaryLight
        BeatStatus.COMPLETED -> InfoSky
        BeatStatus.DRAFT -> MaterialTheme.colorScheme.outline
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("beat_plan_card_${beat.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = beat.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Assigned To: ${beat.executiveName} • Date: ${beat.routeDate}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = status.label,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Stops", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${beat.totalStops} Merchants", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column {
                    Text("Target Collection", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${beat.targetCollection.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column {
                    Text("Achieved", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "₹${beat.achievedCollection.toInt()} (${beat.completedStops} done)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (beat.achievedCollection > 0) SuccessGreen else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (beat.managerNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = beat.managerNotes,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Manager Review Action Buttons if pending
            if (isManager && status == BeatStatus.PENDING_APPROVAL) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCritical),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve Beat", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateBeatPlanDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, date: String, targetCol: Double) -> Unit
) {
    var title by remember { mutableStateOf("Wholesale Market Expansion Route") }
    var routeDate by remember { mutableStateOf("2026-10-06") }
    var targetColStr by remember { mutableStateOf("85000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Plan New Beat Route", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Requires prior approval by your Area Manager before starting execution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Route / Beat Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("beat_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = routeDate,
                    onValueChange = { routeDate = it },
                    label = { Text("Scheduled Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetColStr,
                    onValueChange = { targetColStr = it },
                    label = { Text("Target Collection (₹ INR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPrimaryLight.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BrandPrimaryLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3 pre-selected verified merchant stops will be linked to this beat route automatically.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetCol = targetColStr.toDoubleOrNull() ?: 50000.0
                    onSubmit(title, routeDate, targetCol)
                },
                modifier = Modifier.testTag("submit_beat_for_approval_button")
            ) {
                Text("Submit for Manager Approval")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
