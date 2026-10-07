package com.sharechat.feed.api

import com.sharechat.feed.model.FeedResponse
import com.sharechat.feed.service.FeedService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedController(private val feedService: FeedService) {

    @GetMapping("/v1/feed")
    fun getFeed(
        @RequestParam("user_id") userId: String,
        @RequestParam("lang", required = false) lang: String?,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("limit", defaultValue = "20") limit: Int,
    ): FeedResponse {
        require(limit in 1..50) { "limit must be between 1 and 50" }
        return feedService.getFeed(userId, lang, cursor, limit)
    }
}
