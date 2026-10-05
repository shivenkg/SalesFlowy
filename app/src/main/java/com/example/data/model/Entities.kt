package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PersonaType(val displayName: String, val levelDesc: String) {
    ZONAL_LEAD("Zonal Lead", "Strategic multi-region oversight"),
    REGIONAL_HEAD("Regional Head", "Regional operations & targets"),
    HUB_AREA_MANAGER("Hub / Area Manager", "Supervises Spokes & approves beat plans"),
    SPOKE_FRONTLINER("Sales Executive / Frontliner", "Field customer visits & lead conversion"),
    ADMIN("System Admin", "Dynamic persona & administrative unit setup")
}

enum class HierarchyType {
    ZONE, REGION, HUB, SPOKE
}

enum class AppPermission(val code: String, val title: String, val category: String) {
    VIEW_ACTIVITIES("VIEW_ACTIVITIES", "View Daily Activities & Visits", "Operations"),
    EXECUTE_BEAT("EXECUTE_BEAT", "Execute Beat & GPS Check-In", "Operations"),
    BOOK_ORDERS("BOOK_ORDERS", "Book Orders & Record Sales", "Sales"),
    SUBMIT_BEAT("SUBMIT_BEAT", "Create & Submit Beat Plans", "Operations"),
    APPROVE_BEATS("APPROVE_BEATS", "Approve / Reject Beat Plans", "Management"),
    ADD_LEADS("ADD_LEADS", "Add & Update Leads", "Sales"),
    ONBOARD_MERCHANTS("ONBOARD_MERCHANTS", "Submit Prospective Customers", "Sales"),
    APPROVE_MERCHANTS("APPROVE_MERCHANTS", "Approve Merchant Onboarding", "Management"),
    VIEW_HIERARCHY("VIEW_HIERARCHY", "View Zone/Region/Hub Tree", "Analytics"),
    MANAGE_ADMIN("MANAGE_ADMIN", "Create Personas & Hierarchy Units", "Administration"),
    CLAIM_REWARDS("CLAIM_REWARDS", "Redeem Gamification Rewards", "Gamification")
}

enum class BeatStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

enum class VisitStatus(val label: String) {
    PENDING("Scheduled"),
    CHECKED_IN("Checked In"),
    COMPLETED("Completed"),
    SKIPPED("Skipped")
}

enum class LeadStage(val label: String, val step: Int) {
    PROSPECT("Prospect", 1),
    CONTACTED("Contacted", 2),
    QUALIFIED("Qualified", 3),
    PROPOSAL("Proposal Sent", 4),
    NEGOTIATION("Negotiation", 5),
    WON("Closed Won", 6),
    LOST("Closed Lost", 6)
}

enum class LeadPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // from PersonaType
    val zoneId: String,
    val regionId: String,
    val hubId: String,
    val spokeId: String,
    val targetMonthly: Double,
    val achievedMonthly: Double,
    val points: Int,
    val password: String = "SalesOrbit2026",
    val otp: String = "5432",
    val customPermissions: String = "" // comma-separated AppPermission codes
)

@Entity(tableName = "hierarchy_units")
data class HierarchyUnitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // ZONE, REGION, HUB, SPOKE
    val parentId: String?,
    val leadPersonName: String,
    val activeFrontliners: Int,
    val monthlyTarget: Double,
    val achievedTarget: Double
)

@Entity(tableName = "beat_plans")
data class BeatPlanEntity(
    @PrimaryKey val id: String,
    val executiveId: String,
    val executiveName: String,
    val hubId: String,
    val title: String,
    val routeDate: String, // YYYY-MM-DD
    val status: String, // BeatStatus name
    val managerNotes: String = "",
    val approvedBy: String = "",
    val totalStops: Int = 0,
    val completedStops: Int = 0,
    val targetCollection: Double = 0.0,
    val achievedCollection: Double = 0.0,
    val isOfflineCreated: Boolean = false
)

@Entity(tableName = "beat_stops")
data class BeatStopEntity(
    @PrimaryKey val id: String,
    val beatId: String,
    val stopOrder: Int,
    val customerName: String,
    val customerCategory: String, // Retailer, Wholesaler, Enterprise
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val status: String = "PENDING", // VisitStatus name
    val plannedTime: String = "10:30 AM",
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val checkInLat: Double? = null,
    val checkInLng: Double? = null,
    val interactionSummary: String? = null,
    val customerQuery: String? = null,
    val customerFeedback: String? = null,
    val sentiment: String? = "Positive",
    val orderValue: Double = 0.0,
    val isOfflineRecorded: Boolean = false
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val stage: String, // LeadStage name
    val dealValue: Double,
    val priority: String, // LeadPriority name
    val conversionDeadline: String, // e.g. "Today, 5:00 PM"
    val deadlineHoursRemaining: Int = 4,
    val assignedSpoke: String,
    val assignedExecutiveName: String,
    val notes: String,
    val isAlertActive: Boolean = true,
    val lastUpdated: String = "Just now",
    val isOfflineCreated: Boolean = false
)

@Entity(tableName = "prospect_customers")
data class ProspectCustomerEntity(
    @PrimaryKey val id: String,
    val businessName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val category: String, // FMCG Distributor, Mega Supermarket, Retail Partner
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val gstNumber: String,
    val tradeLicenseNo: String,
    val hasShopPhoto: Boolean = true,
    val hasKycDoc: Boolean = true,
    val status: String = "SUBMITTED_FOR_APPROVAL", // SUBMITTED_FOR_APPROVAL, APPROVED, REJECTED
    val submittedBy: String,
    val submittedByName: String,
    val managerNotes: String = "",
    val submittedDate: String = "Today",
    val isOfflineCreated: Boolean = false
)

@Entity(tableName = "sales_orders")
data class SalesOrderEntity(
    @PrimaryKey val id: String,
    val stopId: String,
    val customerName: String,
    val executiveName: String,
    val orderDate: String,
    val totalAmount: Double,
    val itemsSummary: String,
    val syncStatus: String = "SYNCED" // SYNCED, PENDING_SYNC
)

@Entity(tableName = "alert_notifications")
data class AlertNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // DEADLINE_ALERT, BEAT_APPROVAL, PROSPECT_APPROVAL, TARGET_ACHIEVED
    val priority: String, // CRITICAL, HIGH, NORMAL
    val timestamp: String,
    val isRead: Boolean = false,
    val relatedId: String? = null
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val actionType: String,
    val entityTitle: String = "",
    val payload: String,
    val timestamp: String,
    val status: String = "PENDING" // PENDING, SYNCING, SYNCED, FAILED
)

// UI and Gamification Models
data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val role: String,
    val unitName: String,
    val achievedAmount: Double,
    val targetAmount: Double,
    val conversionRate: Int,
    val points: Int,
    val badge: String,
    val tier: String = "Frontliners"
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val level: String,
    val unlocked: Boolean,
    val iconName: String,
    val currentProgress: Int = 0,
    val targetProgress: Int = 5,
    val xpBonus: Int = 500
)

data class RewardItem(
    val id: String,
    val title: String,
    val category: String,
    val pointsCost: Int,
    val description: String,
    val isClaimed: Boolean = false,
    val claimedDate: String? = null
)

data class MilestoneProgress(
    val id: String,
    val title: String,
    val currentCount: Int,
    val targetCount: Int,
    val unit: String,
    val rewardPoints: Int,
    val badgeUnlocked: String
)

@Entity(tableName = "audit_trail")
data class AuditTrailEntity(
    @PrimaryKey val id: String,
    val executiveId: String,
    val executiveName: String,
    val actionType: String, // GPS_CHECK_IN, VISIT_COMPLETED, ORDER_BOOKED, LEAD_CREATED, LEAD_STAGE_UPDATED, MERCHANT_ONBOARDED, OFFLINE_SYNC, LOGIN
    val entityTitle: String,
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ipAddress: String = "192.168.1.104",
    val timestamp: String,
    val riskLevel: String = "NORMAL", // NORMAL, REVIEW, ANOMALY
    val spokeId: String = "SPOKE-CP"
)

data class AdhocReportQuery(
    val entityType: String = "All Activities", // Visits, Leads, Orders, Merchants, Executive Performance
    val executiveId: String = "ALL",
    val hierarchyTier: String = "ALL", // Zone, Region, Hub, Spoke
    val dateRange: String = "Today", // Today, This Week, This Month, All Time
    val minOrderAmount: Double? = null,
    val queryKeyword: String = ""
)

data class AdhocReportRecord(
    val id: String,
    val title: String,
    val subtitle: String,
    val executive: String,
    val territory: String,
    val amountOrValue: String,
    val status: String,
    val date: String
)

data class AdhocReportResult(
    val queryId: String,
    val generatedAt: String,
    val totalRecords: Int,
    val totalRevenue: Double,
    val records: List<AdhocReportRecord>,
    val summaryInsights: String
)
