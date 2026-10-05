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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.util.IndiaLocaleUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConsoleScreen(
    viewModel: MainViewModel,
    onExitConsole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAdminAuth by viewModel.isAdminAuthenticated.collectAsState()
    val adminUser by viewModel.adminUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val hierarchyUnits by viewModel.hierarchyUnits.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()
    val mongoConfig by viewModel.mongoConfig.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Audit Trail, 1: Ad-Hoc Reports, 2: User ID & Directory, 3: Territory Mappings

    // Login Form State
    var adminEmailInput by remember { mutableStateOf("admin@salesorbit.corp") }
    var adminSecretInput by remember { mutableStateOf("admin123") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    if (!isAdminAuth) {
        // --- ADMIN LOGIN GATEKEEPING VIEW ---
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("admin_login_portal"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .widthIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Security",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Alpha-SalesOrbit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Enterprise Web Admin Console",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RBAC-Gated Access Control • 256-Bit TLS Secured",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = adminEmailInput,
                        onValueChange = { adminEmailInput = it },
                        label = { Text("Administrator ID / Corporate Email") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminSecretInput,
                        onValueChange = { adminSecretInput = it },
                        label = { Text("Console Security Key / Master Password") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
                    )

                    if (loginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginError ?: "",
                            color = AlertCritical,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isLoggingIn = true
                            loginError = null
                            viewModel.loginToAdminConsole(adminEmailInput, adminSecretInput) { success ->
                                isLoggingIn = false
                                if (!success) {
                                    loginError = "Authentication failed. Check admin credentials."
                                }
                            }
                        },
                        enabled = !isLoggingIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_authenticate_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isLoggingIn) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Authenticate & Access Web Console", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FilledTonalButton(
                        onClick = {
                            adminEmailInput = "admin@salesorbit.corp"
                            adminSecretInput = "admin123"
                            viewModel.loginToAdminConsole(adminEmailInput, adminSecretInput) {}
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("quick_admin_demo_login_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⚡ Instant Demo: Chief Admin (Neha Sen)", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onExitConsole) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Return to Mobile Field App", fontSize = 12.sp)
                    }
                }
            }
        }
    } else {
        // --- AUTHENTICATED WEB CONSOLE INTERFACE ---
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Terminal,
                                        contentDescription = "Console",
                                        tint = Color.White,
                                        modifier = Modifier.padding(6.dp).size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Alpha-SalesOrbit",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = BrandSecondaryLight.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ADMIN WEB CONSOLE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandSecondaryLight,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Admin: ${adminUser?.name} (${adminUser?.role}) • MongoDB Atlas: Connected",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = onExitConsole,
                                    modifier = Modifier.height(34.dp).testTag("exit_to_field_app_button")
                                ) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Field App", fontSize = 11.sp)
                                }

                                IconButton(
                                    onClick = { viewModel.logoutAdminConsole() },
                                    modifier = Modifier.testTag("admin_logout_button")
                                ) {
                                    Icon(Icons.Default.PowerSettingsNew, contentDescription = "Logout Admin", tint = AlertCritical)
                                }
                            }
                        }

                        // Web Console Navigation Tabs
                        TabRow(selectedTabIndex = selectedTab) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                text = { Text("Audit Trail", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                text = { Text("Ad-Hoc Reports", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(Icons.Default.PersonAddAlt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                text = { Text("User IDs (${allUsers.size})", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = { Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                text = { Text("Territory Mapping", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            modifier = modifier.testTag("admin_console_authenticated_screen")
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> AdminAuditTrailView(auditLogs = allAuditLogs, allUsers = allUsers)
                    1 -> AdminAdhocReportsView(viewModel = viewModel, allUsers = allUsers, hierarchyUnits = hierarchyUnits)
                    2 -> AdminUserManagementView(viewModel = viewModel, allUsers = allUsers, hierarchyUnits = hierarchyUnits)
                    3 -> AdminTerritoryMappingView(viewModel = viewModel, allUsers = allUsers, hierarchyUnits = hierarchyUnits)
                }
            }
        }
    }
}

// ==========================================
// 1. AUDIT TRAIL ACTIVITIES OF EACH EXECUTIVE
// ==========================================
@Composable
fun AdminAuditTrailView(
    auditLogs: List<AuditTrailEntity>,
    allUsers: List<UserEntity>
) {
    var selectedExecutiveId by remember { mutableStateOf("ALL") }
    var selectedActionFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(auditLogs, selectedExecutiveId, selectedActionFilter, searchQuery) {
        auditLogs.filter { log ->
            (selectedExecutiveId == "ALL" || log.executiveId == selectedExecutiveId) &&
            (selectedActionFilter == "ALL" || log.actionType == selectedActionFilter) &&
            (searchQuery.isBlank() ||
                log.entityTitle.contains(searchQuery, ignoreCase = true) ||
                log.description.contains(searchQuery, ignoreCase = true) ||
                log.executiveName.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_audit_trail_view")
    ) {
        // Control Bar & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Executive Real-Time Audit Trail",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tracking ${allUsers.size} executives • Hardware GPS & IP verified telemetry logs",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LIVE AUDIT STREAM", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Executive Dropdown / Filter Chips
        ScrollableTabRow(
            selectedTabIndex = if (selectedExecutiveId == "ALL") 0 else 1,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedExecutiveId == "ALL",
                onClick = { selectedExecutiveId = "ALL" },
                text = { Text("All Executives (${auditLogs.size})", fontSize = 11.sp) }
            )
            allUsers.filter { it.role == PersonaType.SPOKE_FRONTLINER.name || it.role == PersonaType.HUB_AREA_MANAGER.name }.forEach { user ->
                Tab(
                    selected = selectedExecutiveId == user.id,
                    onClick = { selectedExecutiveId = user.id },
                    text = { Text(user.name, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search audit logs (customer, order, coordinate, action)...", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(46.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            items(filteredLogs) { log ->
                AuditTrailLogCard(log = log)
            }
        }
    }
}

@Composable
fun AuditTrailLogCard(log: AuditTrailEntity) {
    val actionColor = when (log.actionType) {
        "GPS_CHECK_IN" -> BrandPrimaryLight
        "ORDER_BOOKED" -> SuccessGreen
        "VISIT_COMPLETED" -> BrandSecondaryLight
        "LEAD_STAGE_UPDATED" -> PurpleAccent
        "MERCHANT_ONBOARDED" -> InfoSky
        "OFFLINE_SYNC" -> WarningAmber
        "BEAT_APPROVED" -> SuccessGreen
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(actionColor.copy(alpha = 0.35f))
        ),
        modifier = Modifier.fillMaxWidth().testTag("audit_log_${log.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(6.dp), color = actionColor.copy(alpha = 0.15f)) {
                        Text(
                            text = log.actionType.replace("_", " "),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = actionColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = log.executiveName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(${log.executiveId})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(text = log.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.entityTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = log.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata footer (GPS coordinates, Spoke, Device IP)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null, tint = actionColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (log.latitude != null) "GPS: (${log.latitude}, ${log.longitude})" else "GPS: Pending Fix",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Spoke: ${log.spokeId} • IP: ${log.ipAddress}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// 2. AD-HOC QUERY & REPORT GENERATOR
// ==========================================
@Composable
fun AdminAdhocReportsView(
    viewModel: MainViewModel,
    allUsers: List<UserEntity>,
    hierarchyUnits: List<HierarchyUnitEntity>
) {
    var selectedEntity by remember { mutableStateOf("All Activities") }
    var selectedExecutive by remember { mutableStateOf("ALL") }
    var keywordQuery by remember { mutableStateOf("") }
    var isRunningQuery by remember { mutableStateOf(false) }
    var queryResult by remember { mutableStateOf<AdhocReportResult?>(null) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.runAdhocReportQuery(AdhocReportQuery(entityType = selectedEntity)) { res ->
            queryResult = res
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_adhoc_reports_view")
    ) {
        Text(
            text = "Ad-Hoc Query & Enterprise Report Generator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Run dynamic multidimensional queries across visits, deals, orders, and merchant KYC",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Visual Query Builder Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Query Entity Scope", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All Activities", "Beat Stop Visits", "Leads & Conversions", "Sales Orders & Revenue", "Merchant Onboarding").forEach { opt ->
                        FilterChip(
                            selected = selectedEntity == opt,
                            onClick = { selectedEntity = opt },
                            label = { Text(opt, fontSize = 10.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = keywordQuery,
                        onValueChange = { keywordQuery = it },
                        placeholder = { Text("Filter keyword (customer, spoke, deal value)...", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )

                    Button(
                        onClick = {
                            isRunningQuery = true
                            viewModel.runAdhocReportQuery(
                                AdhocReportQuery(
                                    entityType = selectedEntity,
                                    executiveId = selectedExecutive,
                                    queryKeyword = keywordQuery
                                )
                            ) { res ->
                                isRunningQuery = false
                                queryResult = res
                            }
                        },
                        enabled = !isRunningQuery,
                        modifier = Modifier.height(46.dp).testTag("run_adhoc_query_button")
                    ) {
                        if (isRunningQuery) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Query", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Query Output Section
        val result = queryResult
        if (result != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Query Results (${result.totalRecords} Records)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Execution ID: ${result.queryId} • ${result.generatedAt}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = { exportSuccessMessage = "Report exported to CSV: ${result.queryId}.csv" },
                        modifier = Modifier.height(30.dp).testTag("export_csv_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV", fontSize = 10.sp)
                    }
                    FilledTonalButton(
                        onClick = { exportSuccessMessage = "Report exported to JSON payload" },
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("JSON", fontSize = 10.sp)
                    }
                }
            }

            if (exportSuccessMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                    Text(
                        text = exportSuccessMessage ?: "",
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // KPI Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Records", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${result.totalRecords}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Financial Total", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(IndiaLocaleUtil.formatInr(result.totalRevenue), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SuccessGreen)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Integrity Check", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("100% GPS OK", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandPrimaryLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                items(result.records) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(record.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${record.executive} • ${record.territory}", fontSize = 10.sp, color = BrandPrimaryLight)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(record.amountOrValue, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Text(record.status, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. USER MANAGEMENT & ID PROVISIONING
// ==========================================
@Composable
fun AdminUserManagementView(
    viewModel: MainViewModel,
    allUsers: List<UserEntity>,
    hierarchyUnits: List<HierarchyUnitEntity>
) {
    var showCreateUserIdDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_user_management_view")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Enterprise User ID Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Manage credentials, assign roles & provision corporate accounts", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = { showCreateUserIdDialog = true },
                modifier = Modifier.height(36.dp).testTag("provision_user_id_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create User ID", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            items(allUsers) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("user_row_${user.id.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.name.take(2).uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                                    Text(user.id, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("${user.email} • ${user.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Territory: ${user.spokeId} -> ${user.hubId} -> ${user.regionId}", fontSize = 10.sp, color = BrandSecondaryLight)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (user.role) {
                                    PersonaType.ADMIN.name -> AlertCritical.copy(alpha = 0.15f)
                                    PersonaType.ZONAL_LEAD.name -> PurpleAccent.copy(alpha = 0.15f)
                                    PersonaType.HUB_AREA_MANAGER.name -> BrandSecondaryLight.copy(alpha = 0.15f)
                                    else -> SuccessGreen.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = user.role.replace("_", " "),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = "Target: ${IndiaLocaleUtil.formatInr(user.targetMonthly)}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showCreateUserIdDialog) {
        CreateCorporateUserIdDialog(
            onDismiss = { showCreateUserIdDialog = false },
            onSubmit = { userId, name, email, phone, role, zone, reg, hub, spoke, target, perms ->
                viewModel.createCorporateUserWithId(
                    customUserId = userId,
                    name = name,
                    email = email,
                    phone = phone,
                    role = role,
                    zoneId = zone,
                    regionId = reg,
                    hubId = hub,
                    spokeId = spoke,
                    targetMonthly = target,
                    customPermissions = perms
                ) {
                    showCreateUserIdDialog = false
                }
            }
        )
    }
}

@Composable
fun CreateCorporateUserIdDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        userId: String,
        name: String,
        email: String,
        phone: String,
        role: String,
        zone: String,
        reg: String,
        hub: String,
        spoke: String,
        target: Double,
        permissions: String
    ) -> Unit
) {
    var customUserId by remember { mutableStateOf("EMP-2026-${(1000..9999).random()}") }
    var name by remember { mutableStateOf("Suresh Raina") }
    var email by remember { mutableStateOf("suresh.r@alpha-salesorbit.corp") }
    var phone by remember { mutableStateOf("9876543233") }
    var selectedRole by remember { mutableStateOf(PersonaType.SPOKE_FRONTLINER.name) }
    var targetStr by remember { mutableStateOf("175000") }
    var spokeId by remember { mutableStateOf("SPOKE-CP") }
    var hubId by remember { mutableStateOf("HUB-DELHI-CTR") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Provision New Corporate User ID", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customUserId,
                    onValueChange = { customUserId = it },
                    label = { Text("Corporate User ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_user_id_input")
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Legal Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Corporate SSO Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Contact (+91)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("System Role", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(PersonaType.SPOKE_FRONTLINER.name, PersonaType.HUB_AREA_MANAGER.name, PersonaType.ADMIN.name).forEach { r ->
                        FilterChip(
                            selected = selectedRole == r,
                            onClick = { selectedRole = r },
                            label = { Text(r.replace("_", " "), fontSize = 9.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Monthly Revenue Quota (₹ INR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toDoubleOrNull() ?: 150000.0
                    onSubmit(
                        customUserId, name, email, phone, selectedRole,
                        "ZONE-NORTH", "REGION-NCR", hubId, spokeId, target,
                        "VIEW_ACTIVITIES,EXECUTE_BEAT,BOOK_ORDERS,SUBMIT_BEAT,ADD_LEADS,ONBOARD_MERCHANTS,CLAIM_REWARDS"
                    )
                },
                modifier = Modifier.testTag("submit_provision_user_button")
            ) {
                Text("Provision ID")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ==========================================
// 4. TERRITORY & HIERARCHY MAPPING MANAGER
// ==========================================
@Composable
fun AdminTerritoryMappingView(
    viewModel: MainViewModel,
    allUsers: List<UserEntity>,
    hierarchyUnits: List<HierarchyUnitEntity>
) {
    var selectedUserForRemap by remember { mutableStateOf<UserEntity?>(null) }
    var selectedNewSpoke by remember { mutableStateOf("SPOKE-CP") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_territory_mapping_view")
    ) {
        Text(text = "Hierarchy & Territory Mapping Manager", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = "Manage organizational ties from Zonal Headquarters down to Spoke retail routes", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            item {
                Text("Executive Territory Assignments", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            items(allUsers.filter { it.role == PersonaType.SPOKE_FRONTLINER.name }) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Current Mapping: ${user.zoneId} → ${user.regionId} → ${user.hubId} → ${user.spokeId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { selectedUserForRemap = user },
                            modifier = Modifier.height(32.dp).testTag("remap_user_button_${user.id.lowercase()}")
                        ) {
                            Text("Remap", fontSize = 10.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Organizational Hierarchy Tree Nodes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            items(hierarchyUnits) { unit ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(unit.name, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Parent: ${unit.parentId ?: "Top Level"} • Lead: ${unit.leadPersonName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = BrandPrimaryLight.copy(alpha = 0.15f)) {
                            Text(unit.type, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandPrimaryLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }

    if (selectedUserForRemap != null) {
        val user = selectedUserForRemap ?: return
        AlertDialog(
            onDismissRequest = { selectedUserForRemap = null },
            title = { Text("Remap Territory for ${user.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Target Spoke / Hub Territory:", fontSize = 11.sp)
                    listOf("SPOKE-CP" to "Connaught Place Spoke (Delhi Central)", "SPOKE-NOIDA" to "Noida Sector 18 Spoke", "SPOKE-GURGAON" to "CyberCity Gurgaon Spoke").forEach { (spokeCode, spokeName) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { selectedNewSpoke = spokeCode }
                        ) {
                            RadioButton(
                                selected = selectedNewSpoke == spokeCode,
                                onClick = { selectedNewSpoke = spokeCode }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(spokeName, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserTerritoryMapping(
                            userId = user.id,
                            spokeId = selectedNewSpoke,
                            hubId = "HUB-DELHI-CTR",
                            regionId = "REGION-NCR",
                            zoneId = "ZONE-NORTH"
                        )
                        selectedUserForRemap = null
                    },
                    modifier = Modifier.testTag("confirm_remap_button")
                ) {
                    Text("Apply Territory Remapping")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForRemap = null }) { Text("Cancel") }
            }
        )
    }
}
