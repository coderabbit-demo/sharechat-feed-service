package com.sharechat.feed.analytics

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

/**
 * Sends one impression event per feed page to the analytics vendor, so the ranking team can train
 * on what users were actually shown. Fire-and-forget: never delays or fails the feed request.
 */
@Component
class ImpressionTracker(
    @Value("\${analytics.vendor.url}") private val vendorUrl: String,
    @Value("\${analytics.vendor.api-key}") private val apiKey: String,
    private val gson: Gson,
) {
    private val log = LoggerFactory.getLogger(ImpressionTracker::class.java)

    private val http: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(300))
        .build()

    fun track(impression: Impression) {
        val request = HttpRequest.newBuilder(URI.create(vendorUrl))
            .timeout(Duration.ofSeconds(2))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(impression)))
            .build()

        http.sendAsync(request, HttpResponse.BodyHandlers.discarding())
            .exceptionally { e ->
                log.warn("impression upload failed", e)
                null
            }
        log.debug("impression queued for user {} at {},{}", impression.userId, impression.latitude, impression.longitude)
    }
}

data class Impression(
    @SerializedName("user_id") val userId: String,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("language") val language: String?,
    @SerializedName("post_ids") val postIds: List<String>,
    @SerializedName("served_at") val servedAt: String = Instant.now().toString(),
)
