package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesOrbitDao {

    // Users
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier OR id = :identifier LIMIT 1")
    suspend fun findUserByIdentifier(identifier: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Hierarchy Units
    @Query("SELECT * FROM hierarchy_units")
    fun getAllHierarchyUnits(): Flow<List<HierarchyUnitEntity>>

    @Query("SELECT * FROM hierarchy_units WHERE type = :type")
    fun getUnitsByType(type: String): Flow<List<HierarchyUnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHierarchyUnit(unit: HierarchyUnitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHierarchyUnits(units: List<HierarchyUnitEntity>)

    // Beat Plans
    @Query("SELECT * FROM beat_plans ORDER BY routeDate DESC")
    fun getAllBeatPlans(): Flow<List<BeatPlanEntity>>

    @Query("SELECT * FROM beat_plans WHERE executiveId = :executiveId ORDER BY routeDate DESC")
    fun getBeatPlansByExecutive(executiveId: String): Flow<List<BeatPlanEntity>>

    @Query("SELECT * FROM beat_plans WHERE status = 'PENDING_APPROVAL'")
    fun getPendingBeatPlans(): Flow<List<BeatPlanEntity>>

    @Query("SELECT * FROM beat_plans WHERE id = :beatId LIMIT 1")
    suspend fun getBeatPlanById(beatId: String): BeatPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeatPlan(beatPlan: BeatPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeatPlans(beatPlans: List<BeatPlanEntity>)

    @Update
    suspend fun updateBeatPlan(beatPlan: BeatPlanEntity)

    // Beat Stops
    @Query("SELECT * FROM beat_stops")
    fun getAllBeatStops(): Flow<List<BeatStopEntity>>

    @Query("SELECT * FROM beat_stops WHERE beatId = :beatId ORDER BY stopOrder ASC")
    fun getStopsForBeat(beatId: String): Flow<List<BeatStopEntity>>

    @Query("SELECT * FROM beat_stops WHERE id = :stopId LIMIT 1")
    suspend fun getStopById(stopId: String): BeatStopEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeatStop(stop: BeatStopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeatStops(stops: List<BeatStopEntity>)

    @Update
    suspend fun updateBeatStop(stop: BeatStopEntity)

    // Leads
    @Query("SELECT * FROM leads ORDER BY priority DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE priority IN ('HIGH', 'CRITICAL') AND stage NOT IN ('WON', 'LOST')")
    fun getHighPriorityLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    // Prospect Customers
    @Query("SELECT * FROM prospect_customers ORDER BY submittedDate DESC")
    fun getAllProspects(): Flow<List<ProspectCustomerEntity>>

    @Query("SELECT * FROM prospect_customers WHERE status = 'SUBMITTED_FOR_APPROVAL'")
    fun getPendingProspectApprovals(): Flow<List<ProspectCustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProspect(prospect: ProspectCustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProspects(prospects: List<ProspectCustomerEntity>)

    @Update
    suspend fun updateProspect(prospect: ProspectCustomerEntity)

    // Sales Orders
    @Query("SELECT * FROM sales_orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<SalesOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: SalesOrderEntity)

    // Alert Notifications
    @Query("SELECT * FROM alert_notifications ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertNotificationEntity>>

    @Query("SELECT COUNT(*) FROM alert_notifications WHERE isRead = 0")
    fun getUnreadAlertCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertNotificationEntity>)

    @Query("UPDATE alert_notifications SET isRead = 1")
    suspend fun markAllAlertsRead()

    // Sync Queue
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
    suspend fun markItemSynced(id: String)

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearSyncedItems()

    // Audit Trail Activities
    @Query("SELECT * FROM audit_trail ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditTrailEntity>>

    @Query("SELECT * FROM audit_trail WHERE executiveId = :executiveId ORDER BY timestamp DESC")
    fun getAuditLogsByExecutive(executiveId: String): Flow<List<AuditTrailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditTrailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditTrailEntity>)

    // User Territory & Spoke Mapping Updates
    @Query("UPDATE users SET spokeId = :spokeId, hubId = :hubId, regionId = :regionId, zoneId = :zoneId WHERE id = :userId")
    suspend fun updateUserMapping(userId: String, spokeId: String, hubId: String, regionId: String, zoneId: String)
}
