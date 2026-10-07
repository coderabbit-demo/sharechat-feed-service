package com.sharechat.feed.comments.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

// Wire models for the comments API. Field names must match api/openapi.yaml.

data class CreateCommentRequest(
    @SerializedName("text") val text: String,
    // Optional so the migration job can import existing comments with their original state
    @SerializedName("status") val status: CommentStatus?,
    @SerializedName("pinned") val pinned: Boolean?,
)

data class UpdateCommentRequest(
    @SerializedName("text") val text: String,
)

data class HideCommentRequest(
    @SerializedName("reason") val reason: String,
)

data class CommentResponse(
    @SerializedName("id") val id: String,
    @SerializedName("post_id") val postId: String,
    @SerializedName("author_id") val authorId: String,
    @SerializedName("text") val text: String,
    @SerializedName("status") val status: CommentStatus,
    @SerializedName("pinned") val pinned: Boolean,
    @SerializedName("created_at") val createdAt: Instant,
    @SerializedName("edited_at") val editedAt: Instant?,
)

data class CommentPage(
    @SerializedName("items") val items: List<CommentResponse>,
    @SerializedName("next_cursor") val nextCursor: String?,
)

fun Comment.toResponse() = CommentResponse(
    id = id,
    postId = postId,
    authorId = authorId,
    text = text,
    status = status,
    pinned = pinned,
    createdAt = createdAt,
    editedAt = editedAt,
)
