package com.sharechat.feed.api

import com.sharechat.feed.analytics.Impression
import com.sharechat.feed.analytics.ImpressionTracker
import com.sharechat.feed.model.ErrorResponse
import com.sharechat.feed.model.FeedResponse
import com.sharechat.feed.service.FeedService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedController(
    private val feedService: FeedService,
    private val impressionTracker: ImpressionTracker,
) {

    @GetMapping("/v1/feed")
    fun getFeed(
        @RequestParam("user_id") userId: String,
        @RequestParam("lang", required = false) lang: String?,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        @RequestParam("device_id", required = false) deviceId: String?,
        @RequestParam("lat", required = false) lat: Double?,
        @RequestParam("lng", required = false) lng: Double?,
        request: HttpServletRequest,
    ): FeedResponse {
        require(limit in 1..50) { "limit must be between 1 and 50" }
        val feed = feedService.getFeed(userId, lang, cursor, limit)

        impressionTracker.track(
            Impression(
                userId = userId,
                deviceId = deviceId,
                ipAddress = request.remoteAddr,
                latitude = lat,
                longitude = lng,
                language = lang,
                postIds = feed.items.map { it.postId },
            ),
        )
        return feed
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse("invalid_request", e.message ?: "invalid request"))
}
