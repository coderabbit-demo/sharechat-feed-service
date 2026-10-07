package com.sharechat.feed.comments.moderation

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

/** Checks comment text with the trust & safety moderation service (abuse, hate speech, spam). */
@Component
class ModerationClient(
    @Value("\${moderation-service.base-url}") private val baseUrl: String,
    private val gson: Gson,
) {
    private val log = LoggerFactory.getLogger(ModerationClient::class.java)

    private val http: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(100))
        .build()

    fun review(text: String, language: String?): ModerationVerdict =
        try {
            val request = HttpRequest.newBuilder(URI.create("$baseUrl/v1/moderate"))
                .timeout(Duration.ofMillis(150))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ModerationRequest(text, language))))
                .build()
            val response = http.send(request, HttpResponse.BodyHandlers.ofString())
            gson.fromJson(response.body(), ModerationVerdict::class.java)
        } catch (e: Exception) {
            // Don't block people from commenting when moderation is having a bad day
            log.warn("moderation service unavailable, allowing comment", e)
            ModerationVerdict(allowed = true, reason = "moderation_unavailable")
        }
}

data class ModerationRequest(
    @SerializedName("text") val text: String,
    @SerializedName("language") val language: String?,
)

data class ModerationVerdict(
    @SerializedName("allowed") val allowed: Boolean,
    @SerializedName("reason") val reason: String?,
)
