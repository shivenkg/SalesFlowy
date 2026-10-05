package com.example.data.remote.mongodb

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MongoConnectionConfig(
    val clusterName: String = "Cluster0",
    val databaseName: String = "sales_orbit_db",
    val apiKey: String = "",
    val endpointUrl: String = "https://ap-south-1.aws.data.mongodb-api.com/app/data-orbit/endpoint/data/v1/",
    val isConnected: Boolean = true,
    val lastPingMs: Long = 48,
    val totalSyncedDocuments: Int = 18
)

@JsonClass(generateAdapter = true)
data class MongoInsertOneRequest<T>(
    @Json(name = "dataSource") val dataSource: String,
    @Json(name = "database") val database: String,
    @Json(name = "collection") val collection: String,
    @Json(name = "document") val document: T
)

@JsonClass(generateAdapter = true)
data class MongoInsertManyRequest<T>(
    @Json(name = "dataSource") val dataSource: String,
    @Json(name = "database") val database: String,
    @Json(name = "collection") val collection: String,
    @Json(name = "documents") val documents: List<T>
)

@JsonClass(generateAdapter = true)
data class MongoFindRequest(
    @Json(name = "dataSource") val dataSource: String,
    @Json(name = "database") val database: String,
    @Json(name = "collection") val collection: String,
    @Json(name = "filter") val filter: Map<String, String> = emptyMap(),
    @Json(name = "limit") val limit: Int = 10
)

@JsonClass(generateAdapter = true)
data class MongoInsertResponse(
    @Json(name = "insertedId") val insertedId: String? = null,
    @Json(name = "insertedIds") val insertedIds: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class MongoPingResult(
    val status: String,
    val cluster: String,
    val database: String,
    val latencyMs: Long,
    val message: String
)
