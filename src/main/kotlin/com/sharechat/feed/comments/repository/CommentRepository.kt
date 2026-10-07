package com.sharechat.feed.comments.repository

import com.sharechat.feed.comments.model.Comment
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface CommentRepository {
    fun save(comment: Comment): Comment
    fun findById(id: String): Comment?
    fun findByPost(postId: String): List<Comment>
    fun countByPostIds(postIds: Collection<String>): Map<String, Int>
    fun delete(id: String)
}

/** Stand-in for the comments table until the storage migration (FEED-581) lands. */
@Repository
class InMemoryCommentRepository : CommentRepository {

    private val comments = ConcurrentHashMap<String, Comment>()

    override fun save(comment: Comment): Comment {
        comments[comment.id] = comment
        return comment
    }

    override fun findById(id: String): Comment? = comments[id]

    override fun findByPost(postId: String): List<Comment> =
        comments.values.filter { it.postId == postId }.sortedBy { it.createdAt }

    override fun countByPostIds(postIds: Collection<String>): Map<String, Int> {
        val wanted = postIds.toSet()
        return comments.values.filter { it.postId in wanted }.groupingBy { it.postId }.eachCount()
    }

    override fun delete(id: String) {
        comments.remove(id)
    }
}
