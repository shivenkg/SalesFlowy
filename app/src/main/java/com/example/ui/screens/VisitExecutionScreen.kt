package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.BeatStopEntity
import com.example.data.model.VisitStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitExecutionScreen(
    stop: BeatStopEntity,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val gpsCoords by viewModel.currentGpsLocation.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var isCheckedIn by remember { mutableStateOf(stop.status == VisitStatus.CHECKED_IN.name) }
    var checkInTime by remember { mutableStateOf(stop.checkInTime ?: SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())) }
    var checkInLat by remember { mutableStateOf(stop.checkInLat ?: gpsCoords.first) }
    var checkInLng by remember { mutableStateOf(stop.checkInLng ?: gpsCoords.second) }

    var interactionSummary by remember { mutableStateOf(stop.interactionSummary ?: "Met with store manager to review monthly stock movement and introduced seasonal festive SKU displays.") }
    var customerQuery by remember { mutableStateOf(stop.customerQuery ?: "Requested priority dispatch for weekend rush orders.") }
    var customerFeedback by remember { mutableStateOf(stop.customerFeedback ?: "Pleased with product margin structure and promotional branding kit.") }
    var selectedSentiment by remember { mutableStateOf(stop.sentiment ?: "Positive") }

    // SKU Catalog for Order Booking
    var qtyItem1 by remember { mutableStateOf(2) } // Premium Beverage Crate ($2,500)
    var qtyItem2 by remember { mutableStateOf(3) } // Snack Assortment Master Pack ($1,800)
    var qtyItem3 by remember { mutableStateOf(1) } // Dairy & Grocery Bulk Bundle ($4,200)

    val item1Price = 2500.0
    val item2Price = 1800.0
    val item3Price = 4200.0

    val orderTotal = (qtyItem1 * item1Price) + (qtyItem2 * item2Price) + (qtyItem3 * item3Price)
    var paymentTerms by remember { mutableStateOf("Credit (14 Days Net)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stop.customerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stop.customerCategory} • Stop #${stop.stopOrder}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOffline) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WarningAmber.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Offline Mode",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier.testTag("visit_execution_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Step 1: Real-time GPS Location Capture Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCheckedIn) SuccessGreen.copy(alpha = 0.12f) else BrandSecondaryLight.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isCheckedIn) SuccessGreen else BrandSecondaryLight
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gps_capture_card")
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
                                    .background(if (isCheckedIn) SuccessGreen else BrandSecondaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCheckedIn) Icons.Default.GpsFixed else Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isCheckedIn) "Store Visit Verified" else "Store Location Verification",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCheckedIn) SuccessGreen else BrandSecondaryLight
                                )
                                Text(
                                    text = if (isCheckedIn) "Checked In at $checkInTime • Telemetry Active" else "Automated background geofence verification",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isCheckedIn) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen
                            ) {
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Geofence Check", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Within Perimeter", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                        }
                        Column {
                            Text(text = "Location Service", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Background Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandSecondaryLight)
                        }
                        Column {
                            Text(text = "Telemetry Sync", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Area Manager", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (!isCheckedIn) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.startStopCheckIn(stop)
                                isCheckedIn = true
                                checkInTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                checkInLat = gpsCoords.first
                                checkInLng = gpsCoords.second
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("confirm_gps_checkin_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondaryLight)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capture Real-Time GPS & Start Activity", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 2: Record Key Interaction & Discussion
            Text(
                text = "Key Interaction Summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = interactionSummary,
                onValueChange = { interactionSummary = it },
                label = { Text("Discussion Points & Shelf Audit") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interaction_summary_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3: Customer Queries & Concerns
            Text(
                text = "Customer Queries & Support Needs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = customerQuery,
                onValueChange = { customerQuery = it },
                label = { Text("Questions, Lead Times, Credit Queries") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_query_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 4: Customer Feedback & Sentiment
            Text(
                text = "Customer Sentiment & Feedback",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sentiment_selector")
            ) {
                listOf("Positive", "Neutral", "Critical").forEachIndexed { index, sentiment ->
                    SegmentedButton(
                        selected = selectedSentiment == sentiment,
                        onClick = { selectedSentiment = sentiment },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                    ) {
                        Text(sentiment, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = customerFeedback,
                onValueChange = { customerFeedback = it },
                label = { Text("Detailed Feedback Notes") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_feedback_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Step 5: Direct Order Booking on Ground
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Direct Order Booking",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total: ₹${orderTotal.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SKU 1
                    OrderItemRow(
                        name = "Premium Beverage Crate",
                        unitPrice = item1Price,
                        quantity = qtyItem1,
                        onIncrement = { qtyItem1++ },
                        onDecrement = { if (qtyItem1 > 0) qtyItem1-- }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // SKU 2
                    OrderItemRow(
                        name = "Snack Assortment Master Pack",
                        unitPrice = item2Price,
                        quantity = qtyItem2,
                        onIncrement = { qtyItem2++ },
                        onDecrement = { if (qtyItem2 > 0) qtyItem2-- }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // SKU 3
                    OrderItemRow(
                        name = "Dairy & Grocery Bulk Bundle",
                        unitPrice = item3Price,
                        quantity = qtyItem3,
                        onIncrement = { qtyItem3++ },
                        onDecrement = { if (qtyItem3 > 0) qtyItem3-- }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Payment Terms: ", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(6.dp))
                        AssistChip(
                            onClick = {
                                paymentTerms = if (paymentTerms.contains("14")) "Immediate UPI / NEFT" else "Credit (14 Days Net)"
                            },
                            label = { Text(paymentTerms, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step 6: Complete Visit & Check-Out Button
            Button(
                onClick = {
                    viewModel.completeVisit(
                        stopId = stop.id,
                        interactionSummary = interactionSummary,
                        customerQuery = customerQuery,
                        customerFeedback = customerFeedback,
                        sentiment = selectedSentiment,
                        orderValue = orderTotal
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("complete_visit_checkout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete Visit & Check-Out (₹${orderTotal.toInt()})",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderItemRow(
    name: String,
    unitPrice: Double,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "₹${unitPrice.toInt()} / unit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "$quantity",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
