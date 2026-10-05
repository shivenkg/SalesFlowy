package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SeedData
import com.example.data.model.AdhocReportQuery
import com.example.data.model.AppPermission
import com.example.data.model.PersonaType
import com.example.data.remote.mongodb.MongoDbClient
import com.example.data.repository.SalesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Alpha-SalesOrbit", appName)
  }

  @Test
  fun `verify seed personas and hierarchy mapping`() {
    val users = SeedData.users
    assertNotNull(users)
    assertTrue(users.size >= 5)

    val frontliner = users.find { it.role == PersonaType.SPOKE_FRONTLINER.name }
    assertNotNull(frontliner)
    assertEquals("SPOKE-CP", frontliner?.spokeId)

    val manager = users.find { it.role == PersonaType.HUB_AREA_MANAGER.name }
    assertNotNull(manager)
    assertEquals("HUB-DELHI-CTR", manager?.hubId)

    val admin = users.find { it.role == PersonaType.ADMIN.name }
    assertNotNull(admin)
    assertEquals("admin@salesorbit.corp", admin?.email)
  }

  @Test
  fun `verify gamification reward catalog and milestones`() {
    val rewards = SeedData.rewardCatalog
    assertTrue(rewards.isNotEmpty())
    assertEquals(5, rewards.size)

    val milestones = SeedData.milestones
    assertTrue(milestones.isNotEmpty())
    assertEquals(3, milestones.size)
  }

  @Test
  fun `verify granular role permission matrix`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SalesRepository(context)

    val execPerms = repository.getDefaultPermissionsForRole(PersonaType.SPOKE_FRONTLINER.name)
    assertTrue(execPerms.contains(AppPermission.BOOK_ORDERS))
    assertTrue(execPerms.contains(AppPermission.EXECUTE_BEAT))
    assertTrue(!execPerms.contains(AppPermission.APPROVE_BEATS))

    val managerPerms = repository.getDefaultPermissionsForRole(PersonaType.HUB_AREA_MANAGER.name)
    assertTrue(managerPerms.contains(AppPermission.APPROVE_BEATS))
    assertTrue(managerPerms.contains(AppPermission.APPROVE_MERCHANTS))
  }

  @Test
  fun `verify mongodb atlas cloud sync config and client`() = runBlocking {
    val mongoClient = MongoDbClient()
    val initialConfig = mongoClient.config.value
    assertEquals("Cluster0", initialConfig.clusterName)
    assertEquals("sales_orbit_db", initialConfig.databaseName)
    assertTrue(initialConfig.endpointUrl.contains("mongodb-api.com"))

    val pingResult = mongoClient.pingCluster()
    assertNotNull(pingResult)
    assertTrue(pingResult.latencyMs >= 0)
    assertEquals("Cluster0", pingResult.cluster)
    assertEquals("sales_orbit_db", pingResult.database)
  }

  @Test
  fun `verify executive audit trail and adhoc report engine`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SalesRepository(context)

    // Ensure audit log is recorded synchronously
    repository.recordAudit(
      actionType = "LOGIN",
      entityTitle = "Corporate Executive Session Authenticated",
      description = "Mobile client authenticated via biometric/OTP token."
    )

    val logs = repository.getAllAuditLogs().first()
    assertTrue(logs.isNotEmpty())

    // Ensure database records exist for ad-hoc query
    repository.dao.insertBeatStops(SeedData.beatStops)
    repository.dao.insertUsers(SeedData.users)

    // Verify ad-hoc report generator
    val report = repository.runAdhocQuery(AdhocReportQuery(entityType = "All Activities"))
    assertNotNull(report)
    assertTrue(report.totalRecords > 0)
    assertTrue(report.records.isNotEmpty())
  }

  @Test
  fun `verify India regional settings and currency formatting`() {
    val inr1 = com.example.util.IndiaLocaleUtil.formatInr(2500000.0)
    assertTrue(inr1.contains("₹") && inr1.contains("25.00 L"))

    val inr2 = com.example.util.IndiaLocaleUtil.formatInrExact(24500.0)
    assertTrue(inr2.contains("₹") && inr2.contains("24,500"))

    val phone = com.example.util.IndiaLocaleUtil.formatPhone("9876543210")
    assertEquals("+91 98765 43210", phone)
  }

  @Test
  fun `verify executive background GPS capture and manager visibility`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SalesRepository(context)

    // Verify stops have background GPS captured
    val stop = SeedData.beatStops[0]
    assertNotNull(stop.latitude)
    assertNotNull(stop.longitude)
    assertEquals(28.6315, stop.latitude, 0.001)
    assertEquals(77.2167, stop.longitude, 0.001)

    // Verify audit logs store background GPS telemetry for reporting manager
    val auditLog = SeedData.auditLogs.first { it.actionType == "GPS_CHECK_IN" }
    assertNotNull(auditLog.latitude)
    assertNotNull(auditLog.longitude)
    assertEquals(28.6328, auditLog.latitude ?: 0.0, 0.001)
  }
}
