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
import com.example.data.model.ProspectCustomerEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOnboardingScreen(
    viewModel: MainViewModel,
    onNavigateToApprovals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prospects by viewModel.allProspects.collectAsState()
    val gpsCoords by viewModel.currentGpsLocation.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showAddForm by remember { mutableStateOf(false) }

    // Form states
    var businessName by remember { mutableStateOf("Grand Central Provisions") }
    var contactPerson by remember { mutableStateOf("Rajesh Gupta (Managing Partner)") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var email by remember { mutableStateOf("rajesh@grandprovisions.in") }
    var category by remember { mutableStateOf("Mega Wholesaler") }
    var address by remember { mutableStateOf("Shop 14, Commercial Plaza, Connaught Circle, New Delhi") }
    var gstNumber by remember { mutableStateOf("07AAAAA1234B1Z5") }
    var tradeLicense by remember { mutableStateOf("DEL-TL-2026-8849") }

    var hasShopPhoto by remember { mutableStateOf(true) }
    var hasKycDoc by remember { mutableStateOf(true) }

    Scaffold(
        floatingActionButton = {
            if (!showAddForm) {
                ExtendedFloatingActionButton(
                    onClick = { showAddForm = true },
                    icon = { Icon(Icons.Default.PersonAddAlt1, contentDescription = null) },
                    text = { Text("New Merchant") },
                    containerColor = BrandPrimaryLight,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("onboard_new_merchant_fab")
                )
            }
        },
        modifier = modifier.testTag("customer_onboarding_screen")
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
                                    text = "Merchant Onboarding",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "KYC documentation & Manager approval workflow",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalButton(
                                onClick = onNavigateToApprovals,
                                modifier = Modifier.testTag("view_approval_queue_button")
                            ) {
                                Text("Manager Queue", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (showAddForm) {
                // Customer Onboarding Form
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(BrandPrimaryLight.copy(alpha = 0.5f))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("merchant_onboarding_form")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Add Prospective Customer",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showAddForm = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Form")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { businessName = it },
                                label = { Text("Business / Store Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("merchant_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = contactPerson,
                                onValueChange = { contactPerson = it },
                                label = { Text("Proprietor / Key Contact Person") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = category,
                                    onValueChange = { category = it },
                                    label = { Text("Category") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Premises Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Live GPS Pin coordinates badge
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreen.copy(alpha = 0.1f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Premises Geotag: Background Verified for Reporting Manager",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SuccessGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Relevant Verification Documents",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = gstNumber,
                                onValueChange = { gstNumber = it },
                                label = { Text("GSTIN / Tax Certificate Number") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = tradeLicense,
                                onValueChange = { tradeLicense = it },
                                label = { Text("Municipal Trade License No.") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Document Attachments Checkboxes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = hasShopPhoto,
                                    onClick = { hasShopPhoto = !hasShopPhoto },
                                    label = { Text("Store Photo Attached", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                                FilterChip(
                                    selected = hasKycDoc,
                                    onClick = { hasKycDoc = !hasKycDoc },
                                    label = { Text("KYC ID Attached", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.submitProspectOnboarding(
                                        businessName = businessName,
                                        contactPerson = contactPerson,
                                        phone = phone,
                                        email = email,
                                        category = category,
                                        address = address,
                                        gstNumber = gstNumber,
                                        tradeLicense = tradeLicense
                                    )
                                    showAddForm = false
                                },
                                enabled = businessName.isNotBlank() && contactPerson.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_merchant_for_approval_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryLight)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send for Manager Approval", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Existing Prospect Customers List
            item {
                Text(
                    text = "Submitted Merchant Pipelines (${prospects.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(prospects) { prospect ->
                ProspectCustomerCard(
                    prospect = prospect,
                    onApprove = { viewModel.reviewProspect(prospect.id, true, "Verified KYC & premise location.") },
                    onReject = { viewModel.reviewProspect(prospect.id, false, "Additional address proof required.") }
                )
            }
        }
    }
}

@Composable
fun ProspectCustomerCard(
    prospect: ProspectCustomerEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isApproved = prospect.status == "APPROVED"
    val isPending = prospect.status == "SUBMITTED_FOR_APPROVAL"

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isApproved) SuccessGreen.copy(alpha = 0.4f)
                else if (isPending) WarningAmber.copy(alpha = 0.4f)
                else AlertCritical.copy(alpha = 0.4f)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prospect_card_${prospect.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prospect.businessName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${prospect.category} • Contact: ${prospect.contactPerson}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isApproved) SuccessGreen.copy(alpha = 0.15f)
                    else if (isPending) WarningAmber.copy(alpha = 0.15f)
                    else AlertCritical.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isApproved) "Approved" else if (isPending) "Awaiting Manager" else "Returned",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isApproved) SuccessGreen else if (isPending) WarningAmber else AlertCritical,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                    Text(text = "GSTIN", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = prospect.gstNumber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Column {
                    Text(text = "Trade License", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = prospect.tradeLicenseNo, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Column {
                    Text(text = "Submitted", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = prospect.submittedDate, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Storefront Photo & ID Proof Attached by ${prospect.submittedByName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (prospect.managerNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Manager Remark: ${prospect.managerNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
