package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeatStopEntity
import com.example.data.model.PersonaType
import com.example.ui.components.SyncCenterDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

enum class AppScreen(val title: String) {
    ACTIVITIES("Today's Activities"),
    BEATS("Beat Route Plans"),
    LEADS("Sales Pipeline"),
    ONBOARDING("Merchant Onboarding"),
    HIERARCHY("Hierarchy & Admin"),
    GAMIFICATION("Arena & Rewards"),
    APPROVALS("Manager Approvals"),
    NOTIFICATIONS("Priority Alerts"),
    VISIT_EXECUTION("Visit Check-In"),
    ADMIN_CONSOLE("Admin Web Console")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val unreadCount by viewModel.unreadAlertsCount.collectAsState()
            val userMessage by viewModel.userMessage.collectAsState()

            var currentScreen by remember { mutableStateOf(AppScreen.ACTIVITIES) }
            var selectedVisitingStop by remember { mutableStateOf<BeatStopEntity?>(null) }
            var showPersonaMenu by remember { mutableStateOf(false) }
            var showSyncCenterDialog by remember { mutableStateOf(false) }

            val isOffline by viewModel.isOffline.collectAsState()
            val pendingSync by viewModel.pendingSyncItems.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(userMessage) {
                userMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearMessage()
                }
            }

            SalesOrbitTheme(darkTheme = isDarkMode) {
                if (currentUser == null) {
                    AuthScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            currentScreen = AppScreen.ACTIVITIES
                        }
                    )
                } else {
                    BackHandler(enabled = currentScreen != AppScreen.ACTIVITIES) {
                        if (currentScreen == AppScreen.VISIT_EXECUTION) {
                            selectedVisitingStop = null
                        }
                        currentScreen = AppScreen.ACTIVITIES
                    }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (currentScreen == AppScreen.ACTIVITIES) "SalesOrbit" else currentScreen.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (currentScreen == AppScreen.ACTIVITIES) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = BrandSecondaryLight.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "LIVE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = BrandSecondaryLight,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${currentUser?.name} (${PersonaType.entries.find { it.name == currentUser?.role }?.displayName ?: currentUser?.role})",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                navigationIcon = {
                                    if (currentScreen != AppScreen.ACTIVITIES && currentScreen != AppScreen.VISIT_EXECUTION) {
                                        IconButton(onClick = { currentScreen = AppScreen.ACTIVITIES }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Activities")
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(BrandPrimaryLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.TrendingUp,
                                                contentDescription = "Logo",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    // Offline Sync Center Button
                                    IconButton(
                                        onClick = { showSyncCenterDialog = true },
                                        modifier = Modifier.testTag("topbar_sync_center_button")
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (pendingSync.isNotEmpty() || isOffline) {
                                                    Badge(containerColor = if (isOffline) WarningAmber else BrandSecondaryLight) {
                                                        Text(if (isOffline) "!" else "${pendingSync.size}")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudSync,
                                                contentDescription = "Sync Center",
                                                tint = if (isOffline) WarningAmber else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Notifications Badge
                                    IconButton(
                                        onClick = { currentScreen = AppScreen.NOTIFICATIONS },
                                        modifier = Modifier.testTag("topbar_notifications_button")
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (unreadCount > 0) {
                                                    Badge { Text("$unreadCount") }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Notifications"
                                            )
                                        }
                                    }

                                    // Dark/Light Theme Toggle
                                    IconButton(
                                        onClick = { viewModel.toggleDarkMode() },
                                        modifier = Modifier.testTag("theme_toggle_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Theme"
                                        )
                                    }

                                    // Admin Web Console Launcher
                                    IconButton(
                                        onClick = { currentScreen = AppScreen.ADMIN_CONSOLE },
                                        modifier = Modifier.testTag("topbar_admin_console_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Terminal,
                                            contentDescription = "Admin Web Console",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Persona Switcher & Logout Menu
                                    Box {
                                        IconButton(
                                            onClick = { showPersonaMenu = true },
                                            modifier = Modifier.testTag("persona_switcher_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = "User Menu"
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showPersonaMenu,
                                            onDismissRequest = { showPersonaMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Switch to Zonal Lead (Rajesh)") },
                                                onClick = {
                                                    viewModel.switchUser("EMP-ZL-01")
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Switch to Regional Head (Priya)") },
                                                onClick = {
                                                    viewModel.switchUser("EMP-RH-02")
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Switch to Hub Manager (Vikram)") },
                                                onClick = {
                                                    viewModel.switchUser("EMP-AM-03")
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.Hub, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Switch to Frontliner (Amit)") },
                                                onClick = {
                                                    viewModel.switchUser("EMP-EX-04")
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.DirectionsWalk, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Switch to Admin (Neha)") },
                                                onClick = {
                                                    viewModel.switchUser("EMP-ADM-00")
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = { Text("Launch Admin Web Console", fontWeight = FontWeight.Bold) },
                                                onClick = {
                                                    currentScreen = AppScreen.ADMIN_CONSOLE
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = { Text("Sign Out", color = AlertCritical) },
                                                onClick = {
                                                    viewModel.logout()
                                                    showPersonaMenu = false
                                                },
                                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = AlertCritical) }
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            if (currentScreen != AppScreen.VISIT_EXECUTION) {
                                NavigationBar(
                                    modifier = Modifier.testTag("bottom_nav_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.ACTIVITIES,
                                        onClick = { currentScreen = AppScreen.ACTIVITIES },
                                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Today") },
                                        label = { Text("Today", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_today")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.BEATS,
                                        onClick = { currentScreen = AppScreen.BEATS },
                                        icon = { Icon(Icons.Default.AltRoute, contentDescription = "Beats") },
                                        label = { Text("Beats", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_beats")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.LEADS,
                                        onClick = { currentScreen = AppScreen.LEADS },
                                        icon = { Icon(Icons.Default.FilterAlt, contentDescription = "Pipeline") },
                                        label = { Text("Pipeline", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_leads")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.ONBOARDING,
                                        onClick = { currentScreen = AppScreen.ONBOARDING },
                                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Merchants") },
                                        label = { Text("Merchants", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_onboarding")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.HIERARCHY,
                                        onClick = { currentScreen = AppScreen.HIERARCHY },
                                        icon = { Icon(Icons.Default.AccountTree, contentDescription = "Hierarchy") },
                                        label = { Text("Admin", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_hierarchy")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == AppScreen.GAMIFICATION,
                                        onClick = { currentScreen = AppScreen.GAMIFICATION },
                                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Arena") },
                                        label = { Text("Arena", fontSize = 11.sp) },
                                        modifier = Modifier.testTag("nav_item_gamification")
                                    )
                                }
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                AppScreen.ACTIVITIES -> LandingActivitiesScreen(
                                    viewModel = viewModel,
                                    onNavigateToStopExecution = { stop ->
                                        selectedVisitingStop = stop
                                        currentScreen = AppScreen.VISIT_EXECUTION
                                    },
                                    onNavigateToBeats = { currentScreen = AppScreen.BEATS },
                                    onNavigateToLeads = { currentScreen = AppScreen.LEADS },
                                    onNavigateToOnboarding = { currentScreen = AppScreen.ONBOARDING },
                                    onNavigateToAlerts = { currentScreen = AppScreen.NOTIFICATIONS },
                                    onOpenSyncCenter = { showSyncCenterDialog = true }
                                )

                                AppScreen.BEATS -> BeatManagementScreen(
                                    viewModel = viewModel,
                                    onNavigateToApprovals = { currentScreen = AppScreen.APPROVALS }
                                )

                                AppScreen.LEADS -> LeadsPipelineScreen(
                                    viewModel = viewModel
                                )

                                AppScreen.ONBOARDING -> CustomerOnboardingScreen(
                                    viewModel = viewModel,
                                    onNavigateToApprovals = { currentScreen = AppScreen.APPROVALS }
                                )

                                AppScreen.HIERARCHY -> HierarchyAdminScreen(
                                    viewModel = viewModel
                                )

                                AppScreen.GAMIFICATION -> GamificationScreen(
                                    viewModel = viewModel
                                )

                                AppScreen.APPROVALS -> ManagerApprovalsScreen(
                                    viewModel = viewModel
                                )

                                AppScreen.NOTIFICATIONS -> NotificationsScreen(
                                    viewModel = viewModel,
                                    onNavigateToLeads = { currentScreen = AppScreen.LEADS },
                                    onNavigateToBeats = { currentScreen = AppScreen.BEATS }
                                )

                                AppScreen.VISIT_EXECUTION -> {
                                    val stop = selectedVisitingStop
                                    if (stop != null) {
                                        VisitExecutionScreen(
                                            stop = stop,
                                            viewModel = viewModel,
                                            onBack = {
                                                selectedVisitingStop = null
                                                currentScreen = AppScreen.ACTIVITIES
                                            }
                                        )
                                    } else {
                                        currentScreen = AppScreen.ACTIVITIES
                                    }
                                }

                                AppScreen.ADMIN_CONSOLE -> AdminConsoleScreen(
                                    viewModel = viewModel,
                                    onExitConsole = { currentScreen = AppScreen.ACTIVITIES }
                                )
                            }
                        }

                        if (showSyncCenterDialog) {
                            SyncCenterDialog(
                                viewModel = viewModel,
                                onDismiss = { showSyncCenterDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
