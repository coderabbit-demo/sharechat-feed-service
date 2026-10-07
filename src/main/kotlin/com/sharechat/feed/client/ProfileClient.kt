package com.sharechat.feed.client

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/** Reads creator profile stats (followers, verification) from profile-service. */
@Component
class ProfileClient(
    @Value("\${profile-service.base-url}") private val baseUrl: String,
) {
    private val log = LoggerFactory.getLogger(ProfileClient::class.java)

    // Profiles rarely change, so keep the ones we've already fetched
    private val cache = mutableMapOf<String, Profile>()

    fun getProfile(userId: String): Profile {
        cache[userId]?.let { return it }

        return try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder(URI.create("$baseUrl/v1/profiles/$userId")).GET().build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val profile = Gson().fromJson(response.body(), Profile::class.java)
            cache[userId] = profile
            profile
        } catch (e: Exception) {
            log.warn("profile lookup failed for {}", userId, e)
            Profile.UNKNOWN
        }
    }
}

data class Profile(
    @SerializedName("follower_count") val followerCount: Long,
    @SerializedName("is_verified") val isVerified: Boolean,
) {
    companion object {
        val UNKNOWN = Profile(followerCount = 0, isVerified = false)
    }
}
