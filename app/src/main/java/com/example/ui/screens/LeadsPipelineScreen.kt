package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.LeadEntity
import com.example.data.model.LeadPriority
import com.example.data.model.LeadStage
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsPipelineScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val leads by viewModel.allLeads.collectAsState()
    var selectedStageFilter by remember { mutableStateOf<String?>("ALL") }
    var showAddLeadDialog by remember { mutableStateOf(false) }

    val filteredLeads = remember(leads, selectedStageFilter) {
        if (selectedStageFilter == null || selectedStageFilter == "ALL") {
            leads
        } else {
            leads.filter { it.stage == selectedStageFilter }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddLeadDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Lead") },
                containerColor = BrandPrimaryLight,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_lead_fab")
            )
        },
        modifier = modifier.testTag("leads_pipeline_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pipeline Summary Header
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
                                    text = "Automated Lead Pipeline",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Deadlines, conversion triggers & stage tracking",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            val criticalCount = leads.count { it.priority == LeadPriority.CRITICAL.name }
                            if (criticalCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AlertCritical.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Alarm, contentDescription = null, tint = AlertCritical, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$criticalCount Urgent",
                                            color = AlertCritical,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stage Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedStageFilter == "ALL",
                                    onClick = { selectedStageFilter = "ALL" },
                                    label = { Text("All (${leads.size})", fontSize = 11.sp) }
                                )
                            }
                            items(LeadStage.entries.toTypedArray()) { stage ->
                                val count = leads.count { it.stage == stage.name }
                                FilterChip(
                                    selected = selectedStageFilter == stage.name,
                                    onClick = { selectedStageFilter = stage.name },
                                    label = { Text("${stage.label} ($count)", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Leads List
            items(filteredLeads) { lead ->
                LeadItemCard(
                    lead = lead,
                    onAdvanceStage = { nextStage ->
                        viewModel.updateLeadStage(lead.id, nextStage)
                    }
                )
            }
        }
    }

    if (showAddLeadDialog) {
        AddLeadDialog(
            onDismiss = { showAddLeadDialog = false },
            onSubmit = { customerName, contact, phone, email, dealVal, priority, deadline, notes ->
                viewModel.addNewLead(customerName, contact, phone, email, dealVal, priority, deadline, notes)
                showAddLeadDialog = false
            }
        )
    }
}

@Composable
fun LeadItemCard(
    lead: LeadEntity,
    onAdvanceStage: (LeadStage) -> Unit
) {
    val currentStage = try {
        LeadStage.valueOf(lead.stage)
    } catch (_: Exception) {
        LeadStage.PROSPECT
    }

    val isCritical = lead.priority == LeadPriority.CRITICAL.name
    val isHigh = lead.priority == LeadPriority.HIGH.name

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCritical) AlertCritical.copy(alpha = 0.6f)
                else if (isHigh) WarningAmber.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_${lead.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Customer Name + Priority Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${lead.contactPerson} • ${lead.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (lead.priority) {
                        LeadPriority.CRITICAL.name -> AlertCritical.copy(alpha = 0.15f)
                        LeadPriority.HIGH.name -> WarningAmber.copy(alpha = 0.15f)
                        LeadPriority.MEDIUM.name -> InfoSky.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = lead.priority,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (lead.priority) {
                            LeadPriority.CRITICAL.name -> AlertCritical
                            LeadPriority.HIGH.name -> WarningAmber
                            LeadPriority.MEDIUM.name -> InfoSky
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conversion Deadline Alert Banner if Critical / High
            if (isCritical || isHigh) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCritical) AlertCritical.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Deadline",
                        tint = if (isCritical) AlertCritical else WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Conversion Deadline: ${lead.conversionDeadline} (${lead.deadlineHoursRemaining}h remaining)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) AlertCritical else WarningAmber,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Deal Value & Stage Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Deal Value", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "₹${lead.dealValue.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = SuccessGreen)
                }
                Column {
                    Text(text = "Current Stage", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = currentStage.label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandPrimaryLight)
                }
                Column {
                    Text(text = "Assigned", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = lead.assignedExecutiveName, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }

            if (lead.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lead.notes,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stage Action Buttons
            if (currentStage != LeadStage.WON && currentStage != LeadStage.LOST) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (currentStage) {
                        LeadStage.PROSPECT -> {
                            Button(
                                onClick = { onAdvanceStage(LeadStage.QUALIFIED) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Text("Qualify Lead", fontSize = 11.sp)
                            }
                        }
                        LeadStage.CONTACTED, LeadStage.QUALIFIED -> {
                            Button(
                                onClick = { onAdvanceStage(LeadStage.PROPOSAL) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Text("Send Proposal", fontSize = 11.sp)
                            }
                        }
                        LeadStage.PROPOSAL -> {
                            Button(
                                onClick = { onAdvanceStage(LeadStage.NEGOTIATION) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Text("Enter Negotiation", fontSize = 11.sp)
                            }
                        }
                        LeadStage.NEGOTIATION -> {
                            Button(
                                onClick = { onAdvanceStage(LeadStage.WON) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mark Closed Won", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onAdvanceStage(LeadStage.LOST) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCritical),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Text("Mark Lost", fontSize = 11.sp)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun AddLeadDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        customerName: String,
        contact: String,
        phone: String,
        email: String,
        dealVal: Double,
        priority: LeadPriority,
        deadline: String,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dealValStr by remember { mutableStateOf("60000") }
    var selectedPriority by remember { mutableStateOf(LeadPriority.CRITICAL) }
    var deadline by remember { mutableStateOf("Today, 6:00 PM") }
    var notes by remember { mutableStateOf("High-value retail chain expansion. Requires prompt commercial offer.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New High-Priority Lead", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Merchant / Enterprise Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_lead_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Person & Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone (+91)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dealValStr,
                    onValueChange = { dealValStr = it },
                    label = { Text("Deal Value (₹ INR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority Level", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeadPriority.entries.forEach { p ->
                        FilterChip(
                            selected = selectedPriority == p,
                            onClick = { selectedPriority = p },
                            label = { Text(p.name, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Conversion Deadline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Strategic Opportunity Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isNotBlank()) {
                        val dealVal = dealValStr.toDoubleOrNull() ?: 50000.0
                        onSubmit(customerName, contact, phone, email, dealVal, selectedPriority, deadline, notes)
                    }
                },
                modifier = Modifier.testTag("save_new_lead_button")
            ) {
                Text("Create Lead & Trigger Alert")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
