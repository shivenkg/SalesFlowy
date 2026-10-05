package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        HierarchyUnitEntity::class,
        BeatPlanEntity::class,
        BeatStopEntity::class,
        LeadEntity::class,
        ProspectCustomerEntity::class,
        SalesOrderEntity::class,
        AlertNotificationEntity::class,
        SyncQueueEntity::class,
        AuditTrailEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SalesOrbitDatabase : RoomDatabase() {
    abstract fun dao(): SalesOrbitDao

    companion object {
        @Volatile
        private var INSTANCE: SalesOrbitDatabase? = null

        fun getDatabase(context: Context): SalesOrbitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalesOrbitDatabase::class.java,
                    "sales_orbit_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
