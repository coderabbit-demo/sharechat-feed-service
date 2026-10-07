package com.sharechat.feed.service

import com.sharechat.feed.comments.service.CommentService
import com.sharechat.feed.model.Author
import com.sharechat.feed.model.FeedItem
import com.sharechat.feed.model.FeedResponse
import com.sharechat.feed.model.Media
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FeedService(private val comments: CommentService) {

    // Stand-in for the ranked candidates normally returned by the ranking service.
    private val posts = listOf(
        FeedItem(
            postId = "p_1001",
            author = Author("u_501", "priya_sings", "https://cdn.sharechat.example/a/u_501.jpg"),
            media = Media("video", "https://cdn.sharechat.example/v/p_1001.mp4", 31_000),
            caption = "Navratri garba 💃",
            language = "hi",
            likeCount = 1_240_000,
            createdAt = Instant.parse("2026-10-05T18:30:00Z"),
        ),
        FeedItem(
            postId = "p_1002",
            author = Author("u_502", "chennai_foodie", null),
            media = Media("image", "https://cdn.sharechat.example/i/p_1002.jpg", null),
            caption = "Sunday filter coffee ☕",
            language = "ta",
            likeCount = 48_300,
            createdAt = Instant.parse("2026-10-05T04:10:00Z"),
        ),
        FeedItem(
            postId = "p_1003",
            author = Author("u_503", "kolkata_shayari", "https://cdn.sharechat.example/a/u_503.jpg"),
            media = Media("text", "https://cdn.sharechat.example/t/p_1003.txt", null),
            caption = null,
            language = "bn",
            likeCount = 9_870,
            createdAt = Instant.parse("2026-10-04T21:45:00Z"),
        ),
    )

    fun getFeed(userId: String, lang: String?, cursor: String?, limit: Int): FeedResponse {
        val candidates = if (lang == null) posts else posts.filter { it.language == lang }
        val start = cursor?.toIntOrNull() ?: 0
        val slice = candidates.drop(start).take(limit)
        val counts = comments.countsFor(slice.map { it.postId })
        val page = slice.map { it.copy(commentCount = counts[it.postId] ?: 0) }
        val next = (start + slice.size).takeIf { it < candidates.size }?.toString()
        return FeedResponse(items = page, nextCursor = next)
    }
}
