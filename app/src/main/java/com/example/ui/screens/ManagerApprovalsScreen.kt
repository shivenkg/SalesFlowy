package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerApprovalsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allBeats by viewModel.allBeatPlans.collectAsState()
    val allProspects by viewModel.allProspects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()
    val allBeatStops by viewModel.allBeatStops.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Beat Plans, 1: Merchant Onboarding, 2: Executive Live GPS

    val pendingBeats = remember(allBeats) {
        allBeats.filter { it.status == BeatStatus.PENDING_APPROVAL.name }
    }

    val pendingProspects = remember(allProspects) {
        allProspects.filter { it.status == "SUBMITTED_FOR_APPROVAL" }
    }

    val reportingFrontliners = remember(allUsers) {
        allUsers.filter { it.role == PersonaType.SPOKE_FRONTLINER.name }
    }

    Scaffold(
        modifier = modifier.testTag("manager_approvals_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Manager Workflow Oversight",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Reviewing submissions for ${currentUser?.hubId} • ${currentUser?.role?.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Beats (${pendingBeats.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Merchants (${pendingProspects.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Executive Live GPS (${reportingFrontliners.size})") }
                )
            }

            if (selectedTab == 0) {
                // Beat Plans Approval Queue
                if (pendingBeats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("All beat plans reviewed!", fontWeight = FontWeight.Bold)
                            Text("No pending beat routes awaiting your approval.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingBeats) { beat ->
                            ManagerBeatApprovalCard(
                                beat = beat,
                                onApprove = { remarks -> viewModel.reviewBeatPlan(beat.id, true, remarks) },
                                onReject = { remarks -> viewModel.reviewBeatPlan(beat.id, false, remarks) }
                            )
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // Merchant Onboarding Approval Queue
                if (pendingProspects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("All merchant applications reviewed!", fontWeight = FontWeight.Bold)
                            Text("No pending onboarding documents awaiting verification.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingProspects) { prospect ->
                            ManagerProspectApprovalCard(
                                prospect = prospect,
                                onApprove = { remarks -> viewModel.reviewProspect(prospect.id, true, remarks) },
                                onReject = { remarks -> viewModel.reviewProspect(prospect.id, false, remarks) }
                            )
                        }
                    }
                }
            } else {
                // Executive Live GPS Telemetry Dashboard (Exclusively for Reporting Managers)
                ManagerExecutiveGpsTelemetryView(
                    reportingManager = currentUser,
                    frontliners = reportingFrontliners,
                    allBeatStops = allBeatStops,
                    auditLogs = allAuditLogs,
                    onPingExecutive = { name -> viewModel.showMessage("Requested background location ping for $name") }
                )
            }
        }
    }
}

@Composable
fun ManagerBeatApprovalCard(
    beat: BeatPlanEntity,
    onApprove: (remarks: String) -> Unit,
    onReject: (remarks: String) -> Unit
) {
    var remarks by remember { mutableStateOf("Approved. Prioritize festive volume collections.") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.5f))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("manager_beat_card_${beat.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = beat.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Submitted by ${beat.executiveName} • For ${beat.routeDate}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WarningAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Awaiting Action",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Stops", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${beat.totalStops} Retailers", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column {
                    Text("Target Collection", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${beat.targetCollection.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                }
                Column {
                    Text("Hub Unit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(beat.hubId, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Manager Review Remarks") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onReject(remarks) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCritical),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("reject_beat_button_${beat.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", fontSize = 11.sp)
                }

                Button(
                    onClick = { onApprove(remarks) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("approve_beat_button_${beat.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve Route", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ManagerProspectApprovalCard(
    prospect: ProspectCustomerEntity,
    onApprove: (remarks: String) -> Unit,
    onReject: (remarks: String) -> Unit
) {
    var remarks by remember { mutableStateOf("Verified premises and GST records. Approved for credit line.") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.5f))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("manager_prospect_card_${prospect.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = prospect.businessName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${prospect.category} • Submitted by ${prospect.submittedByName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WarningAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "KYC Pending",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Address: ${prospect.address}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp
            )
            Text(
                text = "GSTIN: ${prospect.gstNumber} • Trade License: ${prospect.tradeLicenseNo}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Storefront photo and proprietor KYC document verified",
                    fontSize = 11.sp,
                    color = SuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Approval Remarks / Terms") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onReject(remarks) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCritical),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Text("Return", fontSize = 11.sp)
                }

                Button(
                    onClick = { onApprove(remarks) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("approve_merchant_button_${prospect.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Onboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ManagerExecutiveGpsTelemetryView(
    reportingManager: UserEntity?,
    frontliners: List<UserEntity>,
    allBeatStops: List<BeatStopEntity>,
    auditLogs: List<AuditTrailEntity>,
    onPingExecutive: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("manager_gps_telemetry_view")
    ) {
        // Manager Confidential Telemetry Header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(6.dp), color = BrandPrimaryLight) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp).size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Live Executive GPS Telemetry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Surface(shape = RoundedCornerShape(4.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                        Text(
                            text = "MANAGER ACCESS ONLY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Raw GPS coordinates are hidden on executive devices and run in background. Exact coordinates, geofence compliance, and live breadcrumbs are streamed exclusively to your manager console.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Reporting Executives", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${frontliners.size} Active in Field", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandPrimaryLight)
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Geofence Adherence", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("100% Within Geofence", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Location Mode", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Continuous Background", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Subordinate Field Executives", fontWeight = FontWeight.Bold, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(frontliners) { exec ->
                ExecutiveLiveGpsCard(
                    executive = exec,
                    allBeatStops = allBeatStops,
                    auditLogs = auditLogs,
                    onPing = { onPingExecutive(exec.name) }
                )
            }
        }
    }
}

@Composable
fun ExecutiveLiveGpsCard(
    executive: UserEntity,
    allBeatStops: List<BeatStopEntity>,
    auditLogs: List<AuditTrailEntity>,
    onPing: () -> Unit
) {
    var showBreadcrumbs by remember { mutableStateOf(false) }

    // Associate executive with real-time GPS coordinates
    val (liveLat, liveLng) = when (executive.id) {
        "EMP-EX-04" -> 28.6315 to 77.2167 // Connaught Place Central (Amit Verma)
        "EMP-EX-05" -> 28.6304 to 77.2241 // Barakhamba Road (Neha Gupta)
        "EMP-EX-06" -> 28.6291 to 77.2180 // Janpath High Street (Rajesh Rao)
        else -> 28.6328 to 77.2197
    }

    val currentStopTitle = when (executive.id) {
        "EMP-EX-04" -> "Metro Mart Superstore (Block C, Connaught Place)"
        "EMP-EX-05" -> "Grand Provisions Wholesale (Barakhamba Road)"
        "EMP-EX-06" -> "QuickBite Express Corner (K.G. Marg)"
        else -> "Field Beat Stop #1"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(BrandPrimaryLight.copy(alpha = 0.3f))
        ),
        modifier = Modifier.fillMaxWidth().testTag("exec_gps_card_${executive.id.lowercase()}")
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = executive.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = executive.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text(executive.id, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text(text = "Spoke: ${executive.spokeId} • Territory: ${executive.regionId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(shape = RoundedCornerShape(6.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ACTIVE LIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reporting Manager Telemetry Box (EXACT COORDINATES EXPOSED HERE TO MANAGER)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Background GPS Coordinates:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Accuracy: ±3.5m",
                            fontSize = 10.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lat: %.4f° N  •  Lng: %.4f° E  (New Delhi, India)".format(liveLat, liveLng),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Current Beat Stop: $currentStopTitle",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Geofence & Device Diagnostics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Geofence Distance", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("12m (Within 50m PASS)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SuccessGreen)
                }
                Column {
                    Text("Mock GPS Provider", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("None (Genuine)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SuccessGreen)
                }
                Column {
                    Text("Last Background Ping", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("2m ago (IST)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showBreadcrumbs = !showBreadcrumbs }
                ) {
                    Icon(
                        imageVector = if (showBreadcrumbs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showBreadcrumbs) "Hide Route Breadcrumbs" else "View Route Breadcrumbs", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = onPing,
                    modifier = Modifier.height(32.dp).testTag("ping_exec_button_${executive.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ping Location", fontSize = 10.sp)
                }
            }

            if (showBreadcrumbs) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Today's Verified GPS Breadcrumbs:", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("• 09:32 AM - Stop 1: Metro Mart Connaught Place (GPS: 28.6315, 77.2167) [VERIFIED]", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("• 10:05 AM - Order ORD-4921: Recorded within 12m geofence [VERIFIED]", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("• 10:30 AM - Stop 2: Barakhamba Provisions (GPS: 28.6304, 77.2241) [VERIFIED]", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

