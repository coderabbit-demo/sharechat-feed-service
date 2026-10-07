package com.sharechat.feed.comments.service

import com.sharechat.feed.comments.model.Comment
import com.sharechat.feed.comments.model.CommentStatus
import com.sharechat.feed.comments.model.CreateCommentRequest
import com.sharechat.feed.comments.model.UpdateCommentRequest
import com.sharechat.feed.comments.moderation.ModerationClient
import com.sharechat.feed.comments.repository.CommentRepository
import com.sharechat.feed.security.ForbiddenException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class CommentService(
    private val repository: CommentRepository,
    private val moderation: ModerationClient,
) {

    fun create(postId: String, authorId: String, request: CreateCommentRequest, language: String?): Comment {
        val text = validText(request.text)
        val status = request.status ?: moderate(text, language)

        return repository.save(
            Comment(
                id = UUID.randomUUID().toString(),
                postId = postId,
                authorId = authorId,
                text = text,
                status = status,
                pinned = request.pinned ?: false,
                createdAt = Instant.now(),
            ),
        )
    }

    fun edit(commentId: String, userId: String, request: UpdateCommentRequest): Comment {
        val existing = repository.findById(commentId) ?: throw CommentNotFoundException(commentId)
        val text = validText(request.text)
        return repository.save(existing.copy(text = text, editedAt = Instant.now()))
    }

    fun delete(commentId: String, userId: String) {
        val existing = repository.findById(commentId) ?: throw CommentNotFoundException(commentId)
        if (existing.authorId != userId) throw ForbiddenException("only the author can delete this comment")
        repository.delete(commentId)
    }

    fun hide(commentId: String, moderatorId: String, reason: String): Comment {
        val existing = repository.findById(commentId) ?: throw CommentNotFoundException(commentId)
        return repository.save(existing.copy(status = CommentStatus.HIDDEN, hiddenBy = moderatorId, hiddenReason = reason))
    }

    /** Comments shown under a post, oldest first. Pinned comments come first. */
    fun list(postId: String, cursor: String?, limit: Int): Pair<List<Comment>, String?> {
        val visible = repository.findByPost(postId)
            .filter { it.status != CommentStatus.REJECTED }
            .sortedByDescending { it.pinned }
        val start = cursor?.toIntOrNull() ?: 0
        val page = visible.drop(start).take(limit)
        val next = (start + page.size).takeIf { it < visible.size }?.toString()
        return page to next
    }

    fun countsFor(postIds: Collection<String>): Map<String, Int> = repository.countByPostIds(postIds)

    private fun moderate(text: String, language: String?): CommentStatus =
        if (moderation.review(text, language).allowed) CommentStatus.APPROVED else CommentStatus.REJECTED

    private fun validText(text: String): String {
        val trimmed = text.trim()
        require(trimmed.isNotEmpty()) { "comment text is empty" }
        require(trimmed.length <= MAX_LENGTH) { "comment text is longer than $MAX_LENGTH characters" }
        return trimmed
    }

    companion object {
        const val MAX_LENGTH = 2000
    }
}

class CommentNotFoundException(commentId: String) : RuntimeException("comment $commentId not found")
