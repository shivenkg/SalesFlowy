package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SeedData
import com.example.data.model.*
import com.example.data.remote.mongodb.MongoPingResult
import com.example.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SalesRepository(application)

    val currentUser = repository.currentUser
    val isOffline = repository.isOffline
    val isSyncing = repository.isSyncing
    val isDarkMode = repository.isDarkMode
    val lastSyncTimestamp = repository.lastSyncTimestamp

    val rewardCatalog = repository.rewardCatalog
    val milestones = repository.milestones
    val mongoConfig = repository.mongoConfig

    val allUsers = repository.dao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hierarchyUnits = repository.getAllHierarchyUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBeatPlans = repository.getTodayApprovedBeat()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLeads = repository.getAllLeads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProspects = repository.getAllProspects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlerts = repository.getAllAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAlertsCount = repository.getUnreadAlertCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingSyncItems = repository.getPendingSyncItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBeatStops = repository.dao.getAllBeatStops()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Console Gatekeeping Session
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminUser = MutableStateFlow<UserEntity?>(null)
    val adminUser: StateFlow<UserEntity?> = _adminUser.asStateFlow()

    // Active Beat Stops for Today's Approved Beat
    private val _selectedBeatId = MutableStateFlow("BEAT-TODAY-01")
    val selectedBeatId: StateFlow<String> = _selectedBeatId.asStateFlow()

    private val _todayStops = MutableStateFlow<List<BeatStopEntity>>(emptyList())
    val todayStops: StateFlow<List<BeatStopEntity>> = _todayStops.asStateFlow()

    // GPS status state
    private val _currentGpsLocation = MutableStateFlow(Pair(28.6315, 77.2167)) // Default Connaught Place center
    val currentGpsLocation: StateFlow<Pair<Double, Double>> = _currentGpsLocation.asStateFlow()

    private val _gpsAccuracy = MutableStateFlow("High Precision (±5m)")
    val gpsAccuracy: StateFlow<String> = _gpsAccuracy.asStateFlow()

    // Active Visit In-Progress Stop
    private val _activeVisitingStop = MutableStateFlow<BeatStopEntity?>(null)
    val activeVisitingStop: StateFlow<BeatStopEntity?> = _activeVisitingStop.asStateFlow()

    // UI Snackbars/Messages
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Collect today stops for current selected beat
        viewModelScope.launch {
            repository.getStopsForBeat("BEAT-TODAY-01").collect { stops ->
                _todayStops.value = stops
            }
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun toggleDarkMode() = repository.toggleDarkMode()
    fun toggleOfflineMode() = repository.toggleOfflineMode()
    fun syncNow() = repository.syncPendingItems()

    fun pingMongoDb(onResult: (MongoPingResult) -> Unit) {
        viewModelScope.launch {
            val result = repository.pingMongoDb()
            onResult(result)
            showMessage("MongoDB Ping: ${result.status} (${result.latencyMs}ms)")
        }
    }

    fun updateMongoDbConfig(cluster: String, db: String, key: String, endpoint: String) {
        repository.updateMongoDbConfig(cluster, db, key, endpoint)
        showMessage("MongoDB Atlas configuration updated.")
    }

    fun switchUser(userId: String) {
        repository.switchUser(userId)
        showMessage("Switched context to selected persona")
    }

    fun logout() {
        repository.logout()
    }

    // Role & Permissions check
    fun hasPermission(permission: AppPermission): Boolean {
        return repository.hasPermission(permission)
    }

    fun getPermissionsForCurrentUser(): Set<AppPermission> {
        val user = currentUser.value ?: return emptySet()
        return repository.getPermissionsForUser(user)
    }

    // Gamification: Claim Reward
    fun claimReward(rewardId: String) {
        viewModelScope.launch {
            val success = repository.claimReward(rewardId)
            if (success) {
                showMessage("Reward voucher claimed! Check notifications for redemption code.")
            } else {
                showMessage("Insufficient XP points to redeem this reward.")
            }
        }
    }

    fun updateGpsCoordinates(lat: Double, lng: Double) {
        _currentGpsLocation.value = Pair(lat, lng)
    }

    fun selectBeatForStops(beatId: String) {
        _selectedBeatId.value = beatId
        viewModelScope.launch {
            repository.getStopsForBeat(beatId).collect { stops ->
                _todayStops.value = stops
            }
        }
    }

    fun startStopCheckIn(stop: BeatStopEntity) {
        viewModelScope.launch {
            val (lat, lng) = _currentGpsLocation.value
            repository.checkInToStop(stop.id, lat, lng)
            _activeVisitingStop.value = stop.copy(
                status = VisitStatus.CHECKED_IN.name,
                checkInLat = lat,
                checkInLng = lng
            )
            showMessage("GPS Check-in recorded at ${stop.customerName} ($lat, $lng)")
        }
    }

    fun completeVisit(
        stopId: String,
        interactionSummary: String,
        customerQuery: String,
        customerFeedback: String,
        sentiment: String,
        orderValue: Double
    ) {
        viewModelScope.launch {
            repository.completeStopVisit(
                stopId = stopId,
                interactionSummary = interactionSummary,
                customerQuery = customerQuery,
                customerFeedback = customerFeedback,
                sentiment = sentiment,
                orderValue = orderValue
            )
            _activeVisitingStop.value = null
            showMessage("Visit checked-out & order of ₹${orderValue.toInt()} recorded successfully!")
        }
    }

    fun cancelActiveVisit() {
        _activeVisitingStop.value = null
    }

    fun submitBeatPlan(title: String, date: String, stops: List<BeatStopEntity>, targetAmount: Double) {
        viewModelScope.launch {
            repository.submitBeatPlanForApproval(title, date, stops, targetAmount)
            showMessage("Beat plan '$title' submitted for Area Manager approval")
        }
    }

    fun reviewBeatPlan(beatId: String, approve: Boolean, remarks: String) {
        viewModelScope.launch {
            repository.reviewBeatPlan(beatId, approve, remarks)
            showMessage(if (approve) "Beat plan approved!" else "Beat plan rejected")
        }
    }

    fun addNewLead(
        customerName: String,
        contactPerson: String,
        phone: String,
        email: String,
        dealValue: Double,
        priority: LeadPriority,
        deadline: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val lead = LeadEntity(
                id = "LEAD-${UUID.randomUUID().toString().take(6).uppercase()}",
                customerName = customerName,
                contactPerson = contactPerson,
                phone = phone,
                email = email,
                stage = LeadStage.PROSPECT.name,
                dealValue = dealValue,
                priority = priority.name,
                conversionDeadline = deadline,
                deadlineHoursRemaining = if (priority == LeadPriority.CRITICAL) 4 else 24,
                assignedSpoke = user?.spokeId ?: "SPOKE-CP",
                assignedExecutiveName = user?.name ?: "Sales Executive",
                notes = notes,
                isAlertActive = priority == LeadPriority.CRITICAL || priority == LeadPriority.HIGH
            )
            repository.addNewLead(lead)
            showMessage("Lead '${customerName}' added with ${priority.name} deadline alert!")
        }
    }

    fun updateLeadStage(leadId: String, newStage: LeadStage) {
        viewModelScope.launch {
            repository.updateLeadStage(leadId, newStage)
            showMessage("Lead stage advanced to ${newStage.label}")
        }
    }

    fun submitProspectOnboarding(
        businessName: String,
        contactPerson: String,
        phone: String,
        email: String,
        category: String,
        address: String,
        gstNumber: String,
        tradeLicense: String
    ) {
        viewModelScope.launch {
            val (lat, lng) = _currentGpsLocation.value
            val prospect = ProspectCustomerEntity(
                id = "",
                businessName = businessName,
                contactPerson = contactPerson,
                phone = phone,
                email = email,
                category = category,
                address = address,
                latitude = lat,
                longitude = lng,
                gstNumber = gstNumber,
                tradeLicenseNo = tradeLicense,
                hasShopPhoto = true,
                hasKycDoc = true,
                status = "SUBMITTED_FOR_APPROVAL",
                submittedBy = "",
                submittedByName = ""
            )
            repository.submitProspectCustomer(prospect)
            showMessage("Merchant '$businessName' submitted for Manager Approval with KYC docs!")
        }
    }

    fun reviewProspect(prospectId: String, approve: Boolean, remarks: String) {
        viewModelScope.launch {
            repository.reviewProspect(prospectId, approve, remarks)
            showMessage(if (approve) "Merchant onboarding verified & approved!" else "Merchant application returned")
        }
    }

    fun createHierarchyUnit(name: String, type: HierarchyType, parentId: String?, leadPerson: String, monthlyTarget: Double) {
        viewModelScope.launch {
            val unit = HierarchyUnitEntity(
                id = "UNIT-${UUID.randomUUID().toString().take(6).uppercase()}",
                name = name,
                type = type.name,
                parentId = parentId,
                leadPersonName = leadPerson,
                activeFrontliners = 5,
                monthlyTarget = monthlyTarget,
                achievedTarget = 0.0
            )
            repository.createHierarchyUnit(unit)
            showMessage("New ${type.name} unit '$name' added to hierarchy")
        }
    }

    fun createPersona(
        name: String,
        email: String,
        phone: String,
        role: PersonaType,
        zoneId: String,
        regionId: String,
        hubId: String,
        spokeId: String,
        targetMonthly: Double,
        customPermissions: List<AppPermission> = emptyList()
    ) {
        viewModelScope.launch {
            val permissionsStr = if (customPermissions.isNotEmpty()) {
                customPermissions.joinToString(",") { it.code }
            } else {
                repository.getDefaultPermissionsForRole(role.name).joinToString(",") { it.code }
            }

            val user = UserEntity(
                id = "EMP-${UUID.randomUUID().toString().take(6).uppercase()}",
                name = name,
                email = email,
                phone = phone,
                role = role.name,
                zoneId = zoneId,
                regionId = regionId,
                hubId = hubId,
                spokeId = spokeId,
                targetMonthly = targetMonthly,
                achievedMonthly = 0.0,
                points = 500,
                customPermissions = permissionsStr
            )
            repository.createPersona(user)
            showMessage("New persona '$name' (${role.displayName}) created and mapped with customized permissions!")
        }
    }

    fun markAllAlertsRead() {
        viewModelScope.launch {
            repository.markAllAlertsRead()
        }
    }

    // --- ADMIN CONSOLE METHODS ---

    fun loginToAdminConsole(email: String, secret: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.dao.findUserByIdentifier(email.trim())
            // Admin role or master credentials check
            if ((user != null && (user.role == PersonaType.ADMIN.name || user.role == PersonaType.ZONAL_LEAD.name || user.role == PersonaType.REGIONAL_HEAD.name)) ||
                (email.trim() == "admin@salesorbit.corp" && (secret.trim() == "admin123" || secret.trim() == "1234" || secret.trim() == "5432")) ||
                (secret.trim() == "admin123" || secret.trim() == "1234")) {
                val adminAccount = user ?: repository.dao.getUserById("EMP-ADM-00") ?: SeedData.users.find { it.role == PersonaType.ADMIN.name }
                _isAdminAuthenticated.value = true
                _adminUser.value = adminAccount
                repository.recordAudit(
                    actionType = "ADMIN_CONSOLE_LOGIN",
                    entityTitle = "Admin Console Session Initiated",
                    description = "Authenticated administrator '${adminAccount?.name ?: email}' into Web Console.",
                    risk = "NORMAL"
                )
                showMessage("Admin Console authenticated. Welcome ${adminAccount?.name}!")
                onComplete(true)
            } else {
                showMessage("Invalid credentials or unauthorized administrative role.")
                onComplete(false)
            }
        }
    }

    fun logoutAdminConsole() {
        viewModelScope.launch {
            val admin = _adminUser.value
            repository.recordAudit(
                actionType = "ADMIN_CONSOLE_LOGOUT",
                entityTitle = "Admin Console Session Terminated",
                description = "Administrator '${admin?.name}' signed out of Web Console."
            )
            _isAdminAuthenticated.value = false
            _adminUser.value = null
            showMessage("Admin Console session ended.")
        }
    }

    fun createCorporateUserWithId(
        customUserId: String,
        name: String,
        email: String,
        phone: String,
        role: String,
        zoneId: String,
        regionId: String,
        hubId: String,
        spokeId: String,
        targetMonthly: Double,
        customPermissions: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val newUser = repository.createCorporateUserWithId(
                customUserId = customUserId,
                name = name,
                email = email,
                phone = phone,
                role = role,
                zoneId = zoneId,
                regionId = regionId,
                hubId = hubId,
                spokeId = spokeId,
                targetMonthly = targetMonthly,
                customPermissions = customPermissions
            )
            showMessage("New corporate user ID '${newUser.id}' provisioned successfully!")
            onSuccess()
        }
    }

    fun updateUserTerritoryMapping(
        userId: String,
        spokeId: String,
        hubId: String,
        regionId: String,
        zoneId: String
    ) {
        viewModelScope.launch {
            repository.updateUserTerritoryMapping(userId, spokeId, hubId, regionId, zoneId)
            showMessage("Territory mapping updated for user $userId")
        }
    }

    fun runAdhocReportQuery(query: AdhocReportQuery, onResult: (AdhocReportResult) -> Unit) {
        viewModelScope.launch {
            val result = repository.runAdhocQuery(query)
            repository.recordAudit(
                actionType = "ADHOC_REPORT_GENERATED",
                entityTitle = "Ad-Hoc Query: ${query.entityType}",
                description = "Admin ran ad-hoc report with query '${query.queryKeyword}'. Returned ${result.totalRecords} records."
            )
            onResult(result)
        }
    }
}
