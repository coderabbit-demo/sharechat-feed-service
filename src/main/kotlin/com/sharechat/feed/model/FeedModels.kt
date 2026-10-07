package com.sharechat.feed.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

// Wire models for GET /v1/feed. Field names and types must match api/openapi.yaml.

data class FeedResponse(
    @SerializedName("items") val items: List<FeedItem>,
    @SerializedName("next_cursor") val nextCursor: String?,
)

data class FeedItem(
    @SerializedName("post_id") val postId: String,
    @SerializedName("author") val author: Author,
    @SerializedName("media") val media: Media,
    @SerializedName("caption") val caption: String?,
    @SerializedName("language") val language: String,
    @SerializedName("like_count") val likeCount: Long,
    @SerializedName("created_at") val createdAt: Instant,
)

data class Author(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
)

data class Media(
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("duration_ms") val durationMs: Long?,
)

data class ErrorResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
)
