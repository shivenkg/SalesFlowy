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
import com.example.util.IndiaLocaleUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HierarchyAdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val hierarchyUnits by viewModel.hierarchyUnits.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Hierarchy Tree, 1: Role & Permissions Matrix, 2: Staff & Personas
    var showAddPersonaDialog by remember { mutableStateOf(false) }
    var showAddUnitDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("hierarchy_admin_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hierarchy & RBAC Control",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Zonal → Regional → Hub → Spoke Granular Access",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = { showAddUnitDialog = true },
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("add_unit_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unit", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showAddPersonaDialog = true },
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("add_persona_button")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Persona", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Tab Navigation
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Hierarchy (${hierarchyUnits.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("RBAC Permissions", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Active Staff (${allUsers.size})", fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: HIERARCHY TREE
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(hierarchyUnits) { unit ->
                            HierarchyUnitCard(unit = unit)
                        }
                    }
                }
                1 -> {
                    // TAB 1: ROLE & PERMISSION MATRIX
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Organizational Roles & Access Control Matrix",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(PersonaType.entries.toTypedArray()) { persona ->
                            RolePermissionMatrixCard(
                                persona = persona,
                                defaultPermissions = viewModel.repository.getDefaultPermissionsForRole(persona.name)
                            )
                        }
                    }
                }
                2 -> {
                    // TAB 2: ACTIVE STAFF & PERSONAS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allUsers) { user ->
                            PersonaUserCard(user = user)
                        }
                    }
                }
            }
        }
    }

    if (showAddPersonaDialog) {
        AddPersonaDialog(
            onDismiss = { showAddPersonaDialog = false },
            onSubmit = { name, email, phone, role, zone, reg, hub, spoke, target, perms ->
                viewModel.createPersona(name, email, phone, role, zone, reg, hub, spoke, target, perms)
                showAddPersonaDialog = false
            }
        )
    }

    if (showAddUnitDialog) {
        AddHierarchyUnitDialog(
            onDismiss = { showAddUnitDialog = false },
            onSubmit = { name, type, parentId, leadPerson, target ->
                viewModel.createHierarchyUnit(name, type, parentId, leadPerson, target)
                showAddUnitDialog = false
            }
        )
    }
}

@Composable
fun RolePermissionMatrixCard(
    persona: PersonaType,
    defaultPermissions: Set<AppPermission>
) {
    val roleColor = when (persona) {
        PersonaType.ZONAL_LEAD -> PurpleAccent
        PersonaType.REGIONAL_HEAD -> BrandPrimaryLight
        PersonaType.HUB_AREA_MANAGER -> BrandSecondaryLight
        PersonaType.SPOKE_FRONTLINER -> SuccessGreen
        PersonaType.ADMIN -> AlertCritical
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(roleColor.copy(alpha = 0.4f))
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(roleColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (persona) {
                                PersonaType.ZONAL_LEAD -> Icons.Default.Public
                                PersonaType.REGIONAL_HEAD -> Icons.Default.LocationCity
                                PersonaType.HUB_AREA_MANAGER -> Icons.Default.Hub
                                PersonaType.SPOKE_FRONTLINER -> Icons.Default.DirectionsWalk
                                PersonaType.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            tint = roleColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = persona.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(text = persona.levelDesc, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = roleColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${defaultPermissions.size} Perms",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = roleColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Permission badges flow
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                defaultPermissions.chunked(2).forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chunk.forEach { perm ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = perm.title, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
                        if (chunk.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPersonaDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        email: String,
        phone: String,
        role: PersonaType,
        zone: String,
        reg: String,
        hub: String,
        spoke: String,
        target: Double,
        customPermissions: List<AppPermission>
    ) -> Unit
) {
    var name by remember { mutableStateOf("Nitin Agarwal") }
    var email by remember { mutableStateOf("nitin.a@salesorbit.corp") }
    var phone by remember { mutableStateOf("9876543299") }
    var selectedRole by remember { mutableStateOf(PersonaType.SPOKE_FRONTLINER) }
    var zoneId by remember { mutableStateOf("ZONE-NORTH") }
    var regionId by remember { mutableStateOf("REGION-NCR") }
    var hubId by remember { mutableStateOf("HUB-DELHI-CTR") }
    var spokeId by remember { mutableStateOf("SPOKE-CP") }
    var targetStr by remember { mutableStateOf("175000") }

    // Custom permissions selection
    var selectedPermissions by remember {
        mutableStateOf(
            mutableSetOf(
                AppPermission.VIEW_ACTIVITIES,
                AppPermission.EXECUTE_BEAT,
                AppPermission.BOOK_ORDERS,
                AppPermission.SUBMIT_BEAT,
                AppPermission.ADD_LEADS,
                AppPermission.ONBOARD_MERCHANTS,
                AppPermission.CLAIM_REWARDS
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dynamically Add Missing Persona", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_persona_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Corporate Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (for OTP)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Organizational Role", style = MaterialTheme.typography.labelSmall)
                PersonaType.entries.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedRole == p,
                            onClick = { selectedRole = p }
                        )
                        Text(p.displayName, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Monthly Target ($)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Granular RBAC Permissions Assigned", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                AppPermission.entries.forEach { perm ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPermissions = if (selectedPermissions.contains(perm)) {
                                    selectedPermissions.toMutableSet().apply { remove(perm) }
                                } else {
                                    selectedPermissions.toMutableSet().apply { add(perm) }
                                }
                            }
                            .padding(vertical = 2.dp)
                    ) {
                        Checkbox(
                            checked = selectedPermissions.contains(perm),
                            onCheckedChange = { checked ->
                                selectedPermissions = if (checked) {
                                    selectedPermissions.toMutableSet().apply { add(perm) }
                                } else {
                                    selectedPermissions.toMutableSet().apply { remove(perm) }
                                }
                            }
                        )
                        Text(text = perm.title, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toDoubleOrNull() ?: 150000.0
                    onSubmit(name, email, phone, selectedRole, zoneId, regionId, hubId, spokeId, target, selectedPermissions.toList())
                },
                modifier = Modifier.testTag("save_new_persona_button")
            ) {
                Text("Onboard Persona")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun HierarchyUnitCard(unit: HierarchyUnitEntity) {
    val unitType = try {
        HierarchyType.valueOf(unit.type)
    } catch (_: Exception) {
        HierarchyType.SPOKE
    }

    val typeColor = when (unitType) {
        HierarchyType.ZONE -> PurpleAccent
        HierarchyType.REGION -> BrandPrimaryLight
        HierarchyType.HUB -> BrandSecondaryLight
        HierarchyType.SPOKE -> SuccessGreen
    }

    val progress = if (unit.monthlyTarget > 0) (unit.achievedTarget / unit.monthlyTarget).toFloat() else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(typeColor.copy(alpha = 0.4f))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hierarchy_unit_${unit.id.lowercase()}")
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
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (unitType) {
                                HierarchyType.ZONE -> Icons.Default.Public
                                HierarchyType.REGION -> Icons.Default.LocationCity
                                HierarchyType.HUB -> Icons.Default.Hub
                                HierarchyType.SPOKE -> Icons.Default.Store
                            },
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = unit.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Lead: ${unit.leadPersonName} • ${unit.activeFrontliners} Frontliners",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = typeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = unit.type,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Roll-up Target: ${IndiaLocaleUtil.formatInr(unit.achievedTarget)} / ${IndiaLocaleUtil.formatInr(unit.monthlyTarget)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}% Adherence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = typeColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = typeColor,
                trackColor = typeColor.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun PersonaUserCard(user: UserEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_persona_card_${user.id.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandPrimaryLight.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimaryLight, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${user.id})",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${user.role.replace("_", " ")} • Spoke: ${user.spokeId}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${user.points} XP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldMilestone)
                Text(text = IndiaLocaleUtil.formatInrShort(user.achievedMonthly), fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AddHierarchyUnitDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, type: HierarchyType, parentId: String?, leadPerson: String, target: Double) -> Unit
) {
    var name by remember { mutableStateOf("East Delhi Growth Hub") }
    var selectedType by remember { mutableStateOf(HierarchyType.HUB) }
    var leadPerson by remember { mutableStateOf("Manish Saxena") }
    var targetStr by remember { mutableStateOf("500000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Administrative Unit", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Unit Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_unit_name_input")
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Unit Hierarchy Tier", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HierarchyType.entries.forEach { t ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = { selectedType = t },
                            label = { Text(t.name, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = leadPerson,
                    onValueChange = { leadPerson = it },
                    label = { Text("Designated Lead / Head") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Monthly Target Quota (₹ INR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toDoubleOrNull() ?: 500000.0
                    onSubmit(name, selectedType, "REGION-NCR", leadPerson, target)
                },
                modifier = Modifier.testTag("save_new_unit_button")
            ) {
                Text("Add Unit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
