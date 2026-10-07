package com.sharechat.feed.comments.model

import java.time.Instant

enum class CommentStatus {
    /** Waiting for moderation. */
    PENDING,

    /** Passed moderation and visible to everyone. */
    APPROVED,

    /** Blocked by moderation. */
    REJECTED,

    /** Taken down by a moderator after it was published. */
    HIDDEN,
}

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val status: CommentStatus,
    val pinned: Boolean = false,
    val createdAt: Instant,
    val editedAt: Instant? = null,
    val hiddenBy: String? = null,
    val hiddenReason: String? = null,
)
